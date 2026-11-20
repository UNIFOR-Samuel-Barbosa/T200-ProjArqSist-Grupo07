package br.edu.unifor.missaomarte.gdx;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BotaoTest {
    @Test
    void cliqueDentroDoRetanguloIncluindoBordasEForaNao() {
        Botao botao = new Botao("1  Jogar", 100, 50, 200, 40, () -> { });

        assertTrue(botao.contem(100, 50));
        assertTrue(botao.contem(300, 90));
        assertTrue(botao.contem(200, 70));
        assertFalse(botao.contem(99, 70));
        assertFalse(botao.contem(200, 91));
    }

    @Test
    void acaoDoBotaoEExecutadaSemDependerDaTela() {
        AtomicInteger cliques = new AtomicInteger();
        Botao botao = new Botao("Decolar", 0, 0, 10, 10, cliques::incrementAndGet);

        botao.acao().run();

        assertEquals(1, cliques.get());
    }
}
