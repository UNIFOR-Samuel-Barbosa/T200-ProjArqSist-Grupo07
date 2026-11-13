package br.edu.unifor.missaomarte.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NaveTest {
    @Test
    void respeitaCapacidadeEEsvasiaAListaNaEntrega() {
        Nave nave = new Nave(new Posicao(0, 1), 1);
        Passageiro passageiro = new Professor("Ari", new Posicao(1, 1));

        assertTrue(nave.embarcar(passageiro));
        assertFalse(nave.embarcar(new Engenheiro("Bia", new Posicao(2, 1))));
        assertEquals(1, nave.passageiros().size());
        assertEquals(1, nave.desembarcarTodos());
        assertTrue(nave.passageiros().isEmpty());
    }
}
