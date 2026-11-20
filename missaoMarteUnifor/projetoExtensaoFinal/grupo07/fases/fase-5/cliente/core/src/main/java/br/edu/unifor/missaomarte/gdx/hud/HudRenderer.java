package br.edu.unifor.missaomarte.gdx.hud;

import br.edu.unifor.missaomarte.application.EstadoPartida;
import br.edu.unifor.missaomarte.application.GameSession;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.gdx.Paleta;
import br.edu.unifor.missaomarte.gdx.recursos.Imagem;
import br.edu.unifor.missaomarte.gdx.recursos.Recursos;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;

/**
 * HUD da partida, desenhado depois do mundo para ficar por cima. Apenas lê a {@link GameSession}:
 * barra superior (vidas, pontos, tempo, som), painel lateral (piloto, vagas, entregas, mensagens) e rodapé.
 */
public final class HudRenderer {
    private static final float PAINEL_X = 532;
    private static final float PAINEL_LARGURA = 256;

    private final SpriteBatch batch;
    private final ShapeRenderer formas;
    private final BitmapFont fonte;
    private final Recursos recursos;

    public HudRenderer(SpriteBatch batch, ShapeRenderer formas, BitmapFont fonte, Recursos recursos) {
        this.batch = batch;
        this.formas = formas;
        this.fonte = fonte;
        this.recursos = recursos;
    }

    public void desenhar(GameSession sessao, int vidasIniciais, String mensagem, boolean mudo,
                         String avisoDoFim, String instrucaoDoFim) {
        Missao missao = sessao.missao();
        int capacidade = missao.nave().capacidade();
        float progresso = missao.totalPassageiros() == 0 ? 1f
                : (float) sessao.passageirosEntregues() / missao.totalPassageiros();

        painel(12, 552, 776, 42);
        painel(PAINEL_X, 54, PAINEL_LARGURA, 484);
        painel(12, 6, 776, 34);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        formas.begin(ShapeRenderer.ShapeType.Filled);
        formas.setColor(Paleta.BARRA_FUNDO);
        formas.rect(PAINEL_X + 14, 352, PAINEL_LARGURA - 28, 14);
        formas.setColor(Paleta.BARRA);
        formas.rect(PAINEL_X + 14, 352, (PAINEL_LARGURA - 28) * progresso, 14);
        formas.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.begin();
        batch.setColor(1, 1, 1, 1);
        for (int i = 0; i < vidasIniciais; i++) {
            Imagem icone = i < missao.nave().vidas() ? Imagem.VIDA_CHEIA : Imagem.VIDA_VAZIA;
            batch.draw(recursos.regiao(icone), 22 + i * 30, 558, 28, 28);
        }
        texto("Pontos: " + sessao.pontuacao(), 140, 580, Paleta.TEXTO);
        texto("Tempo: " + formatarTempo(sessao.duracaoSegundos()), 330, 580, Paleta.TEXTO);
        texto(mudo ? "Som: desligado (M)" : "Som: ligado (M)", 620, 580, Paleta.TEXTO);

        float x = PAINEL_X + 14;
        texto("Piloto: " + sessao.piloto(), x, 522, Paleta.DESTAQUE);
        texto("Dificuldade: " + sessao.dificuldade(), x, 498, Paleta.TEXTO);
        texto("Vagas na nave", x, 466, Paleta.TEXTO);
        int aBordo = missao.nave().passageiros().size();
        for (int i = 0; i < capacidade; i++) {
            Imagem icone = i < aBordo ? Imagem.VAGA_OCUPADA : Imagem.VAGA_LIVRE;
            batch.draw(recursos.regiao(icone), x + i * 34, 420, 30, 30);
        }
        texto("Entregas: " + sessao.passageirosEntregues() + "/" + missao.totalPassageiros(), x, 392, Paleta.TEXTO);
        texto("Restantes no mapa: " + missao.passageiros().size(), x, 334, Paleta.TEXTO);
        texto("Movimentos: " + sessao.movimentos(), x, 310, Paleta.TEXTO);
        texto("Último turno:", x, 276, Paleta.DESTAQUE);
        fonte.setColor(Paleta.TEXTO);
        fonte.draw(batch, mensagem, x, 254, PAINEL_LARGURA - 28, Align.left, true);

        texto("W/A/S/D ou setas: mover (segure para repetir)   E: embarcar   Q/Esc: abortar   M: som",
                24, 29, Paleta.TEXTO);
        batch.end();

        if (sessao.estado() == EstadoPartida.VITORIA || sessao.estado() == EstadoPartida.DERROTA) {
            desenharFim(sessao, avisoDoFim, instrucaoDoFim);
        }
    }

    private void desenharFim(GameSession sessao, String aviso, String instrucao) {
        boolean venceu = sessao.estado() == EstadoPartida.VITORIA;
        painel(62, 200, 420, 180);
        batch.begin();
        texto(venceu ? "VITÓRIA! Missão concluída!" : "FIM DE JOGO... a missão falhou", 90, 355, Paleta.DESTAQUE);
        texto("Pontos: " + sessao.pontuacao() + "   Tempo: " + formatarTempo(sessao.duracaoSegundos()),
                90, 318, Paleta.TEXTO);
        if (aviso != null) {
            fonte.setColor(Paleta.DESTAQUE);
            fonte.draw(batch, aviso, 90, 272, 370, Align.left, true);
        }
        texto(instrucao, 90, 230, Paleta.TEXTO);
        batch.end();
    }

    public static String formatarTempo(long segundos) {
        return String.format("%02d:%02d", segundos / 60, segundos % 60);
    }

    private void painel(float x, float y, float largura, float altura) {
        painel(batch, recursos, x, y, largura, altura);
    }

    /** Painel com a moldura da arte (NinePatch); chamar fora de batch.begin()/end(). */
    public static void painel(SpriteBatch batch, Recursos recursos, float x, float y, float largura, float altura) {
        batch.begin();
        batch.setColor(1, 1, 1, 1);
        recursos.painel().draw(batch, x, y, largura, altura);
        batch.end();
    }

    private void texto(String texto, float x, float y, Color cor) {
        fonte.setColor(cor);
        fonte.draw(batch, texto, x, y);
    }
}
