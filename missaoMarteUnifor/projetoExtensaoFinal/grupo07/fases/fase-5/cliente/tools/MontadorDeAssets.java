import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;

/**
 * Monta core/assets a partir da arte gerada no Gemini (tools/arte-gemini) e sintetiza os sons.
 *
 * Uso, na raiz da fase-4:  java tools/MontadorDeAssets.java
 *
 * Imagens: remove o fundo branco por preenchimento a partir das bordas, recorta, reduz com qualidade
 * e monta as faixas de quadros (as animações são derivadas da mesma arte: escala, brilho, transparência).
 * Sons: gerados por síntese (ondas senoidais e triangulares com envelope), sem arquivos de terceiros.
 * As dimensões precisam bater com o enum Imagem do core (um teste confere).
 */
public class MontadorDeAssets {
    private static final int Q = 64;
    private static final File ARTE = new File("tools/arte-gemini");
    private static final File IMAGENS = new File("core/assets/imagens");
    private static final File SONS = new File("core/assets/sons");
    private static final float TAXA = 22050f;

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        IMAGENS.mkdirs();
        SONS.mkdirs();
        imagens();
        sons();
        System.out.println("Assets montados em core/assets");
    }

    // ------------------------------------------------------------------ imagens

    private static void imagens() throws IOException {
        salvar(fundo(), "fundo.png");
        BufferedImage terreno = ler("terreno");
        int margem = terreno.getWidth() / 9; // corta a borda arredondada para as células se encaixarem
        salvar(suavizarCelula(redimensionar(terreno.getSubimage(margem, margem, terreno.getWidth() - 2 * margem,
                terreno.getHeight() - 2 * margem), Q, Q)), "terreno.png");
        salvar(quadro(sprite("nave"), 1f, 1f, 1f, 0, 0), "nave.png");

        BufferedImage chama = sprite("propulsor");
        salvar(faixa(chamaQuadro(chama, 0.70f, 0.85f), chamaQuadro(chama, 0.85f, 1f), chamaQuadro(chama, 1f, 0.9f)),
                "propulsor.png");

        BufferedImage base = sprite("base");
        salvar(faixa(quadro(base, 1f, 1f, 1f, 0, 0), clarear(quadro(base, 1.05f, 1.05f, 1f, 0, 0), 0.22f)), "base.png");

        BufferedImage inimigo = sprite("inimigo");
        salvar(faixa(quadro(inimigo, 1f, 1f, 1f, 0, 0), quadro(inimigo, 1.07f, 0.93f, 1f, 0, -2)), "inimigo.png");

        for (String nome : new String[] {"asteroide", "professor", "engenheiro", "astronauta", "desconhecido"}) {
            salvar(quadro(sprite(nome), 1f, 1f, 1f, 0, 0), nome + ".png");
        }

        BufferedImage explosao = sprite("explosao");
        salvar(faixa(quadro(explosao, 0.45f, 0.45f, 1f, 0, 0), quadro(explosao, 0.75f, 0.75f, 1f, 0, 0),
                quadro(explosao, 1f, 1f, 0.85f, 0, 0), quadro(explosao, 1.1f, 1.1f, 0.4f, 0, 0)), "explosao.png");

        BufferedImage brilho = sprite("brilho");
        salvar(faixa(girar(quadro(brilho, 0.5f, 0.5f, 0.9f, 0, 0), 0), girar(quadro(brilho, 1f, 1f, 1f, 0, 0), 15),
                girar(quadro(brilho, 0.85f, 0.85f, 0.5f, 0, 0), 30)), "brilho.png");

        BufferedImage botao = redimensionar(caber(sprite("botao"), 256, 64), 256, 64);
        salvar(botao, "botao.png");
        salvar(trocarRosaPorMenta(botao), "botao_destaque.png");
        salvar(redimensionar(sprite("painel"), 192, 192), "painel.png");

        BufferedImage coracao = sprite("coracao");
        salvar(redimensionar(ajustarCaixa(coracao, 0.92f), 32, 32), "vida_cheia.png");
        salvar(apagar(redimensionar(ajustarCaixa(coracao, 0.92f), 32, 32)), "vida_vazia.png");
        BufferedImage vaga = sprite("vaga");
        salvar(redimensionar(ajustarCaixa(vaga, 0.92f), 32, 32), "vaga_ocupada.png");
        salvar(apagar(redimensionar(ajustarCaixa(vaga, 0.92f), 32, 32)), "vaga_livre.png");
    }

    private static BufferedImage ler(String nome) throws IOException {
        return ImageIO.read(new File(ARTE, nome + ".jpg"));
    }

    /** Arte com o fundo branco removido e recortada no conteúdo. */
    private static BufferedImage sprite(String nome) throws IOException {
        return recortar(removerFundo(ler(nome)));
    }

    /**
     * Torna transparente o branco ligado às bordas (o branco de dentro do desenho, cercado pelo contorno,
     * é preservado). As cores dos pixels removidos viram a do contorno para não criar halo branco ao reduzir.
     */
    private static BufferedImage removerFundo(BufferedImage origem) {
        int w = origem.getWidth();
        int h = origem.getHeight();
        BufferedImage saida = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        boolean[] fundo = new boolean[w * h];
        ArrayDeque<Integer> fila = new ArrayDeque<>();
        for (int x = 0; x < w; x++) {
            fila.add(x);
            fila.add((h - 1) * w + x);
        }
        for (int y = 0; y < h; y++) {
            fila.add(y * w);
            fila.add(y * w + w - 1);
        }
        while (!fila.isEmpty()) {
            int i = fila.poll();
            if (fundo[i] || !claro(origem.getRGB(i % w, i / w))) {
                continue;
            }
            fundo[i] = true;
            int x = i % w;
            int y = i / w;
            if (x > 0) fila.add(i - 1);
            if (x < w - 1) fila.add(i + 1);
            if (y > 0) fila.add(i - w);
            if (y < h - 1) fila.add(i + w);
        }
        for (int i = 0; i < w * h; i++) {
            int rgb = origem.getRGB(i % w, i / w) & 0xFFFFFF;
            saida.setRGB(i % w, i / w, fundo[i] ? 0x003C244A : 0xFF000000 | rgb);
        }
        return saida;
    }

    private static boolean claro(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        int max = Math.max(r, Math.max(g, b));
        int min = Math.min(r, Math.min(g, b));
        return min > 222 && max - min < 28;
    }

    private static BufferedImage recortar(BufferedImage img) {
        int x0 = img.getWidth(), y0 = img.getHeight(), x1 = 0, y1 = 0;
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                if ((img.getRGB(x, y) >>> 24) > 16) {
                    x0 = Math.min(x0, x);
                    y0 = Math.min(y0, y);
                    x1 = Math.max(x1, x);
                    y1 = Math.max(y1, y);
                }
            }
        }
        return img.getSubimage(x0, y0, x1 - x0 + 1, y1 - y0 + 1);
    }

    /** Centraliza a arte em um quadrado, ocupando a fração pedida do lado. */
    private static BufferedImage ajustarCaixa(BufferedImage img, float ocupacao) {
        int lado = Math.round(Math.max(img.getWidth(), img.getHeight()) / ocupacao);
        BufferedImage saida = new BufferedImage(lado, lado, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = saida.createGraphics();
        g.drawImage(img, (lado - img.getWidth()) / 2, (lado - img.getHeight()) / 2, null);
        g.dispose();
        return saida;
    }

    /** Centraliza a arte em um retângulo da proporção pedida, sem deformar. */
    private static BufferedImage caber(BufferedImage img, int largura, int altura) {
        double escala = Math.max((double) img.getWidth() / largura, (double) img.getHeight() / altura);
        int w = (int) Math.ceil(largura * escala);
        int h = (int) Math.ceil(altura * escala);
        BufferedImage saida = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = saida.createGraphics();
        g.drawImage(img, (w - img.getWidth()) / 2, (h - img.getHeight()) / 2, null);
        g.dispose();
        return saida;
    }

    /** Variante do botão para a opção destacada: troca os canais vermelho e verde (rosa vira menta). */
    private static BufferedImage trocarRosaPorMenta(BufferedImage img) {
        BufferedImage saida = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int p = img.getRGB(x, y);
                saida.setRGB(x, y, (p & 0xFF0000FF) | ((p >> 8) & 0xFF) << 16 | ((p >> 16) & 0xFF) << 8);
            }
        }
        return saida;
    }

    /** Redução em etapas (metade por vez) com alfa pré-multiplicado: bordas suaves sem halo. */
    private static BufferedImage redimensionar(BufferedImage img, int largura, int altura) {
        BufferedImage atual = img;
        int w = img.getWidth();
        int h = img.getHeight();
        do {
            w = Math.max(largura, w / 2);
            h = Math.max(altura, h / 2);
            BufferedImage proxima = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB_PRE);
            Graphics2D g = proxima.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.drawImage(atual, 0, 0, w, h, null);
            g.dispose();
            atual = proxima;
        } while (w != largura || h != altura);
        BufferedImage saida = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = saida.createGraphics();
        g.drawImage(atual, 0, 0, null);
        g.dispose();
        return saida;
    }

    /** Um quadro 64x64: a arte em escala (x, y), com transparência e deslocamento. */
    private static BufferedImage quadro(BufferedImage arte, float escalaX, float escalaY, float alfa, int dx, int dy) {
        BufferedImage base = redimensionar(ajustarCaixa(arte, 0.9f), Q, Q);
        BufferedImage saida = new BufferedImage(Q, Q, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = saida.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alfa));
        int w = Math.round(Q * escalaX);
        int h = Math.round(Q * escalaY);
        g.drawImage(base, (Q - w) / 2 + dx, (Q - h) / 2 + dy, w, h, null);
        g.dispose();
        return saida;
    }

    /** Quadro do propulsor: a chama aponta para baixo e encolhe a partir do topo (perto da nave). */
    private static BufferedImage chamaQuadro(BufferedImage chama, float comprimento, float alfa) {
        BufferedImage base = redimensionar(ajustarCaixa(chama, 0.95f), Q, Q);
        BufferedImage saida = new BufferedImage(Q, Q, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = saida.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alfa));
        g.drawImage(base, 0, 0, Q, Math.round(Q * comprimento), null);
        g.dispose();
        return saida;
    }

    private static BufferedImage girar(BufferedImage img, double graus) {
        BufferedImage saida = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = saida.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(img, AffineTransform.getRotateInstance(Math.toRadians(graus), img.getWidth() / 2.0, img.getHeight() / 2.0), null);
        g.dispose();
        return saida;
    }

    private static BufferedImage clarear(BufferedImage img, float quanto) {
        BufferedImage saida = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int p = img.getRGB(x, y);
                int r = (int) (((p >> 16) & 0xFF) + (255 - ((p >> 16) & 0xFF)) * quanto);
                int gr = (int) (((p >> 8) & 0xFF) + (255 - ((p >> 8) & 0xFF)) * quanto);
                int b = (int) ((p & 0xFF) + (255 - (p & 0xFF)) * quanto);
                saida.setRGB(x, y, (p & 0xFF000000) | r << 16 | gr << 8 | b);
            }
        }
        return saida;
    }

    /**
     * A textura do Gemini é detalhada demais para 121 células: mistura 80% com a cor média (fica sutil)
     * e escurece 1 pixel de borda para a grade aparecer.
     */
    private static BufferedImage suavizarCelula(BufferedImage img) {
        long r = 0, g = 0, b = 0;
        int n = img.getWidth() * img.getHeight();
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int p = img.getRGB(x, y);
                r += (p >> 16) & 0xFF;
                g += (p >> 8) & 0xFF;
                b += p & 0xFF;
            }
        }
        int mr = (int) (r / n), mg = (int) (g / n), mb = (int) (b / n);
        BufferedImage saida = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int p = img.getRGB(x, y);
                boolean borda = x == 0 || y == 0 || x == img.getWidth() - 1 || y == img.getHeight() - 1;
                float f = borda ? 0.88f : 1f;
                int cr = (int) ((((p >> 16) & 0xFF) * 0.2f + mr * 0.8f) * f);
                int cg = (int) ((((p >> 8) & 0xFF) * 0.2f + mg * 0.8f) * f);
                int cb = (int) (((p & 0xFF) * 0.2f + mb * 0.8f) * f);
                saida.setRGB(x, y, 0xFF000000 | cr << 16 | cg << 8 | cb);
            }
        }
        return saida;
    }

    /** Versão "vazia" de um ícone: cinza-lilás e translúcida. */
    private static BufferedImage apagar(BufferedImage img) {
        BufferedImage saida = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int p = img.getRGB(x, y);
                int cinza = (((p >> 16) & 0xFF) + ((p >> 8) & 0xFF) + (p & 0xFF)) / 3;
                int alfa = (int) ((p >>> 24) * 0.45f);
                saida.setRGB(x, y, alfa << 24 | Math.min(255, cinza + 20) << 16 | cinza << 8 | Math.min(255, cinza + 30));
            }
        }
        return saida;
    }

    private static BufferedImage faixa(BufferedImage... quadros) {
        BufferedImage saida = new BufferedImage(Q * quadros.length, Q, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = saida.createGraphics();
        for (int i = 0; i < quadros.length; i++) {
            g.drawImage(quadros[i], i * Q, 0, null);
        }
        g.dispose();
        return saida;
    }

    /** Fundo 800x600: corta a faixa central 4:3 da arte quadrada. */
    private static BufferedImage fundo() throws IOException {
        BufferedImage arte = ler("fundo");
        int altura = arte.getWidth() * 3 / 4;
        BufferedImage corte = arte.getSubimage(0, (arte.getHeight() - altura) / 2, arte.getWidth(), altura);
        BufferedImage saida = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = saida.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(corte, 0, 0, 800, 600, null);
        g.dispose();
        return saida;
    }

    private static void salvar(BufferedImage img, String nome) throws IOException {
        ImageIO.write(img, "png", new File(IMAGENS, nome));
    }

    // ------------------------------------------------------------------ sons

    private static void sons() throws IOException {
        float[] mover = varredura(620, 940, 0.06f, 0.35f);
        salvarSom(mover, "mover.wav");
        salvarSom(juntar(nota(988, 0.05f, 0.4f), nota(1319, 0.07f, 0.4f)), "selecionar.wav");
        salvarSom(juntar(nota(1047, 0.07f, 0.45f), nota(1319, 0.07f, 0.45f), nota(1568, 0.12f, 0.45f)), "embarcar.wav");
        salvarSom(juntar(nota(1047, 0.06f, 0.4f), nota(1319, 0.06f, 0.4f), nota(1568, 0.06f, 0.4f),
                nota(2093, 0.25f, 0.45f)), "entregar.wav");
        salvarSom(misturar(varredura(420, 110, 0.35f, 0.55f), ruido(0.12f, 0.25f)), "colisao.wav");
        salvarSom(juntar(nota(523, 0.12f, 0.45f), nota(659, 0.12f, 0.45f), nota(784, 0.12f, 0.45f),
                nota(1047, 0.18f, 0.45f), nota(784, 0.1f, 0.45f), nota(1047, 0.45f, 0.5f)), "vitoria.wav");
        salvarSom(juntar(nota(392, 0.22f, 0.4f), nota(330, 0.22f, 0.4f), nota(262, 0.55f, 0.4f)), "derrota.wav");
        salvarSom(musica(), "musica.wav");
    }

    /** Caixinha de música em laço (8 compassos, escala pentatônica) com baixo suave. */
    private static float[] musica() {
        int[] melodia = {72, 76, 79, 76, 81, 79, 76, 74, 72, 74, 76, 79, 76, 74, 72, -1,
                69, 72, 76, 72, 74, 76, 79, 81, 79, 76, 74, 76, 72, 74, 72, -1};
        int[] baixo = {48, 48, 53, 53, 55, 55, 48, 48};
        float tempo = 0.25f;
        float[] saida = new float[(int) (TAXA * tempo * melodia.length)];
        for (int i = 0; i < melodia.length; i++) {
            if (melodia[i] > 0) {
                somar(saida, (int) (i * tempo * TAXA), nota(midi(melodia[i]), tempo * 1.6f, 0.22f));
            }
            if (i % 4 == 0) {
                somar(saida, (int) (i * tempo * TAXA), nota(midi(baixo[i / 4]), tempo * 3.8f, 0.14f));
            }
        }
        return saida;
    }

    private static float midi(int nota) {
        return (float) (440 * Math.pow(2, (nota - 69) / 12.0));
    }

    /** Nota com timbre de sino (seno + harmônico) e envelope com ataque rápido. */
    private static float[] nota(float freq, float duracao, float volume) {
        float[] s = new float[(int) (TAXA * duracao)];
        for (int i = 0; i < s.length; i++) {
            double t = i / TAXA;
            double env = Math.min(1, t / 0.005) * Math.exp(-t * 4.5 / duracao);
            s[i] = (float) (volume * env * (Math.sin(2 * Math.PI * freq * t) + 0.3 * Math.sin(4 * Math.PI * freq * t)));
        }
        return s;
    }

    private static float[] varredura(float de, float ate, float duracao, float volume) {
        float[] s = new float[(int) (TAXA * duracao)];
        double fase = 0;
        for (int i = 0; i < s.length; i++) {
            double p = (double) i / s.length;
            double freq = de + (ate - de) * p + 18 * Math.sin(p * 40);
            fase += 2 * Math.PI * freq / TAXA;
            s[i] = (float) (volume * Math.sin(fase) * Math.min(1, p * 30) * (1 - p));
        }
        return s;
    }

    private static float[] ruido(float duracao, float volume) {
        java.util.Random r = new java.util.Random(7);
        float[] s = new float[(int) (TAXA * duracao)];
        for (int i = 0; i < s.length; i++) {
            s[i] = (float) (volume * (r.nextDouble() * 2 - 1) * (1 - (double) i / s.length));
        }
        return s;
    }

    private static float[] juntar(float[]... partes) {
        int total = 0;
        for (float[] p : partes) total += p.length;
        float[] s = new float[total];
        int pos = 0;
        for (float[] p : partes) {
            System.arraycopy(p, 0, s, pos, p.length);
            pos += p.length;
        }
        return s;
    }

    private static float[] misturar(float[] a, float[] b) {
        float[] s = new float[Math.max(a.length, b.length)];
        somar(s, 0, a);
        somar(s, 0, b);
        return s;
    }

    private static void somar(float[] destino, int inicio, float[] parte) {
        for (int i = 0; i < parte.length && inicio + i < destino.length; i++) {
            destino[inicio + i] += parte[i];
        }
    }

    private static void salvarSom(float[] amostras, String nome) throws IOException {
        byte[] pcm = new byte[amostras.length * 2];
        for (int i = 0; i < amostras.length; i++) {
            short v = (short) (Math.max(-1, Math.min(1, amostras[i])) * 32000);
            pcm[2 * i] = (byte) v;
            pcm[2 * i + 1] = (byte) (v >> 8);
        }
        AudioFormat formato = new AudioFormat(TAXA, 16, 1, true, false);
        try (AudioInputStream entrada = new AudioInputStream(new ByteArrayInputStream(pcm), formato, amostras.length)) {
            AudioSystem.write(entrada, AudioFileFormat.Type.WAVE, new File(SONS, nome));
        }
    }
}
