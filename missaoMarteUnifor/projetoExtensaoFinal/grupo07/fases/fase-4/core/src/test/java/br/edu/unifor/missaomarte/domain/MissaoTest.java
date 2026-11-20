package br.edu.unifor.missaomarte.domain;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class MissaoTest {
    @Test
    void inimigosNaoOcupamCelulasDePassageirosDuranteMovimento() {
        Posicao posicaoPassageiro = new Posicao(1, 0);

        for (long seed = 0; seed < 100; seed++) {
            Inimigo inimigo = new Inimigo(new Posicao(0, 0));
            Missao missao = new Missao(new Nave(new Posicao(-1, 0), 1),
                    new Base(new Posicao(-2, 0)), 5,
                    List.of(new Professor("Ari", posicaoPassageiro)), List.of(), List.of(inimigo));

            missao.moverInimigos(new Random(seed));

            assertNotEquals(posicaoPassageiro, inimigo.posicao());
            assertEquals(posicaoPassageiro, missao.passageiros().get(0).posicao());
        }
    }
}
