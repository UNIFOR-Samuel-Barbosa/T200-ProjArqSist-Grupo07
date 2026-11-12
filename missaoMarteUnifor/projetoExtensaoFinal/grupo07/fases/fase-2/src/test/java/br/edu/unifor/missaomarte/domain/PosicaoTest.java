package br.edu.unifor.missaomarte.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class PosicaoTest {
    @Test
    void deslocamentoCriaPosicaoImutavelComIgualdadePorCoordenadas() {
        Posicao origem = new Posicao(2, -1);
        Posicao destino = origem.deslocar(1, 3);

        assertEquals(new Posicao(3, 2), destino);
        assertEquals(new Posicao(2, -1), origem);
        assertNotSame(origem, destino);
        assertEquals(new Posicao(2, -1).hashCode(), origem.hashCode());
    }
}
