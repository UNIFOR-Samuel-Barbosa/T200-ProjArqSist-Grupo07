package br.edu.unifor.missaomarte.application;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AcaoTest {
    @Test
    void traduzComandosERejeitaEntradaDesconhecida() {
        assertEquals(Acao.CIMA, Acao.interpretar("W"));
        assertEquals(Acao.DIREITA, Acao.interpretar("d"));
        assertEquals(Acao.EMBARCAR, Acao.interpretar("e"));
        assertEquals(Acao.ABORTAR, Acao.interpretar("q"));
        assertEquals(Acao.INVALIDA, Acao.interpretar("?"));
        assertEquals(Acao.INVALIDA, Acao.interpretar(null));
    }
}
