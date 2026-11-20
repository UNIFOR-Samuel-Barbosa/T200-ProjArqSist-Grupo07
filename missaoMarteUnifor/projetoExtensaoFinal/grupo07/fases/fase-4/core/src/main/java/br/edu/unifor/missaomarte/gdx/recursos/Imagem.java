package br.edu.unifor.missaomarte.gdx.recursos;

/**
 * Catálogo das imagens do jogo. Não depende do libGDX: só descreve arquivo, tamanho do quadro e
 * quantidade de quadros. Imagens animadas são faixas horizontais de quadros do mesmo tamanho.
 */
public enum Imagem {
    FUNDO("fundo.png", 800, 600, 1),
    TERRENO("terreno.png", 64, 64, 1),
    NAVE("nave.png", 64, 64, 1),
    PROPULSOR("propulsor.png", 64, 64, 3),
    BASE("base.png", 64, 64, 2),
    ASTEROIDE("asteroide.png", 64, 64, 1),
    INIMIGO("inimigo.png", 64, 64, 2),
    PROFESSOR("professor.png", 64, 64, 1),
    ENGENHEIRO("engenheiro.png", 64, 64, 1),
    ASTRONAUTA("astronauta.png", 64, 64, 1),
    DESCONHECIDO("desconhecido.png", 64, 64, 1),
    EXPLOSAO("explosao.png", 64, 64, 4),
    BRILHO("brilho.png", 64, 64, 3),
    VIDA_CHEIA("vida_cheia.png", 32, 32, 1),
    VIDA_VAZIA("vida_vazia.png", 32, 32, 1),
    VAGA_OCUPADA("vaga_ocupada.png", 32, 32, 1),
    VAGA_LIVRE("vaga_livre.png", 32, 32, 1),
    BOTAO("botao.png", 256, 64, 1),
    BOTAO_DESTAQUE("botao_destaque.png", 256, 64, 1),
    PAINEL("painel.png", 192, 192, 1);

    public static final String PASTA = "imagens/";

    private final String arquivo;
    private final int larguraQuadro;
    private final int alturaQuadro;
    private final int quadros;

    Imagem(String arquivo, int larguraQuadro, int alturaQuadro, int quadros) {
        this.arquivo = arquivo;
        this.larguraQuadro = larguraQuadro;
        this.alturaQuadro = alturaQuadro;
        this.quadros = quadros;
    }

    public String arquivo() {
        return arquivo;
    }

    public String caminho() {
        return PASTA + arquivo;
    }

    public int larguraQuadro() {
        return larguraQuadro;
    }

    public int alturaQuadro() {
        return alturaQuadro;
    }

    public int quadros() {
        return quadros;
    }
}
