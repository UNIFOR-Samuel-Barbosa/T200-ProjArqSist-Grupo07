package br.edu.unifor.missaomarte.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DificuldadeTest {
    @Test
    void interpretaNomesComEsemAcento() {
        assertEquals(Dificuldade.FACIL, Dificuldade.tentarInterpretar("facil").orElseThrow());
        assertEquals(Dificuldade.MEDIO, Dificuldade.tentarInterpretar("MÉDIO").orElseThrow());
        assertEquals(Dificuldade.DIFICIL, Dificuldade.tentarInterpretar("difícil").orElseThrow());
    }

    @Test
    void entradaDesconhecidaNaoEscolheUmaDificuldadeSilenciosamente() {
        assertTrue(Dificuldade.tentarInterpretar("especialista").isEmpty());
        assertTrue(Dificuldade.tentarInterpretar(null).isEmpty());
    }
}
