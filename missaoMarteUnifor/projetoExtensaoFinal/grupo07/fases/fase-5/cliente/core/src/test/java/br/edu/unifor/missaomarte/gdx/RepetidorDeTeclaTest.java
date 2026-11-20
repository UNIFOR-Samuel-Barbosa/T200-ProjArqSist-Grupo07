package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.Acao;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RepetidorDeTeclaTest {
    @Test
    void esperaOAtrasoDepoisRepeteACadaIntervalo() {
        RepetidorDeTecla repetidor = new RepetidorDeTecla();
        repetidor.pressionar(Acao.DIREITA);

        assertNull(repetidor.avancar(RepetidorDeTecla.ATRASO - 0.01f));
        assertEquals(Acao.DIREITA, repetidor.avancar(0.02f));
        assertNull(repetidor.avancar(RepetidorDeTecla.INTERVALO - 0.03f));
        assertEquals(Acao.DIREITA, repetidor.avancar(0.03f));
    }

    @Test
    void noMaximoUmaRepeticaoPorChamada() {
        RepetidorDeTecla repetidor = new RepetidorDeTecla();
        repetidor.pressionar(Acao.CIMA);

        assertEquals(Acao.CIMA, repetidor.avancar(5f));
        assertEquals(Acao.CIMA, repetidor.avancar(0.0002f));
    }

    @Test
    void paraAoSoltarEReiniciaOAtrasoAoPressionarDeNovo() {
        RepetidorDeTecla repetidor = new RepetidorDeTecla();
        repetidor.pressionar(Acao.BAIXO);
        repetidor.soltar(Acao.BAIXO);
        assertNull(repetidor.avancar(1f));

        repetidor.pressionar(Acao.BAIXO);
        assertNull(repetidor.avancar(0.1f));
    }

    @Test
    void acaoQueNaoEMovimentoNaoRepete() {
        RepetidorDeTecla repetidor = new RepetidorDeTecla();
        repetidor.pressionar(Acao.EMBARCAR);

        assertNull(repetidor.avancar(1f));
    }
}
