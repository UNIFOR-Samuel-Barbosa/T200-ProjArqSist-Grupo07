package br.edu.unifor.missaomarte.gdx.visual;

import br.edu.unifor.missaomarte.domain.Asteroide;
import br.edu.unifor.missaomarte.domain.Nave;
import br.edu.unifor.missaomarte.domain.ObjetoRenderizavel;
import br.edu.unifor.missaomarte.domain.Posicao;
import br.edu.unifor.missaomarte.gdx.recursos.Imagem;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Lógica de apresentação pura: posição visual, efeitos, textos e tremor. */
class VisualTest {
    private static final float ERRO = 1e-4f;

    @Test
    void objetoNovoNaoDeslizaEAoMudarDeCelulaPersegueOAlvoSemUltrapassar() {
        Nave nave = new Nave(new Posicao(0, 0), 1);
        PosicoesVisuais posicoes = new PosicoesVisuais(10);
        posicoes.atualizar(List.of(nave), 0.016f);
        assertEquals(0, posicoes.ponto(nave).x(), ERRO);

        nave.mover(1, 0, 5);
        posicoes.atualizar(List.of(nave), 0.05f);
        assertEquals(0.5f, posicoes.ponto(nave).x(), ERRO);

        posicoes.atualizar(List.of(nave), 1f);
        assertEquals(1f, posicoes.ponto(nave).x(), ERRO);
    }

    @Test
    void naDiagonalAndaEmLinhaReta() {
        Nave nave = new Nave(new Posicao(0, 0), 1);
        PosicoesVisuais posicoes = new PosicoesVisuais(1);
        posicoes.atualizar(List.of(nave), 0);
        nave.mover(1, 0, 5);
        nave.mover(0, 1, 5);

        posicoes.atualizar(List.of(nave), 0.5f);

        PosicoesVisuais.Ponto ponto = posicoes.ponto(nave);
        assertEquals(ponto.x(), ponto.y(), ERRO);
        assertEquals(0.5f, (float) Math.hypot(ponto.x(), ponto.y()), ERRO);
    }

    @Test
    void objetosQueSomemSaoEsquecidosEAComparacaoEPorIdentidade() {
        ObjetoRenderizavel a = new Asteroide(new Posicao(1, 1));
        ObjetoRenderizavel igualMasOutro = new Asteroide(new Posicao(1, 1));
        PosicoesVisuais posicoes = new PosicoesVisuais();

        posicoes.atualizar(List.of(a, igualMasOutro), 0);
        assertEquals(2, posicoes.quantidade());
        posicoes.atualizar(List.of(a), 0);
        assertEquals(1, posicoes.quantidade());
    }

    @Test
    void efeitoDistribuiQuadrosPelaDuracaoESomeAoTerminar() {
        EfeitoUnico explosao = new EfeitoUnico(Imagem.EXPLOSAO, 0, 0, 0.4f);

        assertEquals(0, explosao.quadroAtual());
        assertTrue(explosao.avancar(0.15f));
        assertEquals(1, explosao.quadroAtual());
        assertTrue(explosao.avancar(0.2f));
        assertEquals(3, explosao.quadroAtual());
        assertFalse(explosao.avancar(0.1f));
    }

    @Test
    void textoFlutuanteSobeFicaTransparenteESome() {
        TextoFlutuante texto = new TextoFlutuante("+10", 2, 3);

        texto.avancar(TextoFlutuante.DURACAO / 2);

        assertTrue(texto.y() < 3, "subir na tela diminui o y da célula");
        assertEquals(0.5f, texto.alfa(), ERRO);
        assertFalse(texto.avancar(TextoFlutuante.DURACAO));
    }

    @Test
    void variacaoDePontosSoApareceQuandoMaiorQueUm() {
        assertNull(TextoFlutuante.variacaoDePontos(-1, 0, 0));
        assertNull(TextoFlutuante.variacaoDePontos(0, 0, 0));
        assertEquals("-6", TextoFlutuante.variacaoDePontos(-6, 0, 0).texto());
        assertEquals("+15", TextoFlutuante.variacaoDePontos(15, 0, 0).texto());
    }

    @Test
    void tremorComecaForteDecaiETerminaEmZero() {
        Tremor tremor = new Tremor(10, 1);
        assertEquals(0, tremor.intensidade(), ERRO);

        tremor.disparar();
        assertEquals(10, tremor.intensidade(), ERRO);
        tremor.avancar(0.5f);
        assertEquals(5, tremor.intensidade(), ERRO);
        tremor.avancar(2);
        assertEquals(0, tremor.intensidade(), ERRO);
        assertEquals(0, tremor.deslocamentoX(), ERRO);
    }
}
