package br.edu.unifor.missaomarte.application;

import br.edu.unifor.missaomarte.domain.Asteroide;
import br.edu.unifor.missaomarte.domain.Base;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.Nave;
import br.edu.unifor.missaomarte.domain.Passageiro;
import br.edu.unifor.missaomarte.domain.Posicao;
import br.edu.unifor.missaomarte.domain.Professor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameSessionEventosTest {
    private final AtomicLong relogio = new AtomicLong(1_000);

    /** Passageiro distante mantém a partida em andamento (missão sem passageiros já nasce vencida). */
    private GameSession sessao(Nave nave, int pontos, List<Asteroide> asteroides, Professor... passageiros) {
        List<Passageiro> lista = passageiros.length == 0 ? List.of(new Professor("Longe", new Posicao(5, 5))) : List.of(passageiros);
        Missao missao = new Missao(nave, new Base(new Posicao(0, 0)), 5, lista, asteroides, List.of());
        return new GameSession(missao, new Random(1), "Piloto", "Fácil", pontos, relogio::get);
    }

    @Test
    void movimentoGeraEventoDeMovimento() {
        ResultadoTurno resultado = sessao(new Nave(new Posicao(0, 1), 1), 30, List.of()).processar(Acao.DIREITA);

        assertEquals(List.of(EventoTurno.MOVIMENTO), resultado.eventos());
    }

    @Test
    void movimentoBarradoNoLimiteNaoGeraMovimento() {
        ResultadoTurno resultado = sessao(new Nave(new Posicao(5, 0), 1), 30, List.of()).processar(Acao.DIREITA);

        assertTrue(resultado.consumiuTurno());
        assertFalse(resultado.teve(EventoTurno.MOVIMENTO));
    }

    @Test
    void embarqueEntregaEVitoria() {
        GameSession sessao = sessao(new Nave(new Posicao(1, 0), 1), 30, List.of(),
                new Professor("Lia", new Posicao(1, 0)));

        assertEquals(List.of(EventoTurno.EMBARQUE), sessao.processar(Acao.EMBARCAR).eventos());
        ResultadoTurno entrega = sessao.processar(Acao.ESQUERDA);

        assertEquals(List.of(EventoTurno.MOVIMENTO, EventoTurno.ENTREGA, EventoTurno.VITORIA), entrega.eventos());
    }

    @Test
    void colisaoEDerrota() {
        GameSession sessao = sessao(new Nave(new Posicao(0, 1), 1), 6, List.of(new Asteroide(new Posicao(1, 1))));

        ResultadoTurno resultado = sessao.processar(Acao.DIREITA);

        assertEquals(List.of(EventoTurno.MOVIMENTO, EventoTurno.COLISAO, EventoTurno.DERROTA), resultado.eventos());
    }

    @Test
    void comandoInvalidoEAbortarNaoGeramEventos() {
        GameSession sessao = sessao(new Nave(new Posicao(0, 1), 1), 30, List.of());

        assertTrue(sessao.processar(Acao.INVALIDA).eventos().isEmpty());
        assertTrue(sessao.processar(Acao.ABORTAR).eventos().isEmpty());
    }

    @Test
    void construtorAntigoDoResultadoContinuaValidoESemEventos() {
        ResultadoTurno resultado = new ResultadoTurno(false, "ok", EstadoPartida.EM_ANDAMENTO);

        assertTrue(resultado.eventos().isEmpty());
    }

    @Test
    void duracaoParaDeCorrerQuandoAPartidaTermina() {
        GameSession sessao = sessao(new Nave(new Posicao(0, 1), 1), 6, List.of(new Asteroide(new Posicao(1, 1))));
        relogio.addAndGet(4_500);
        assertEquals(4, sessao.duracaoSegundos());

        sessao.processar(Acao.DIREITA);
        relogio.addAndGet(60_000);

        assertEquals(EstadoPartida.DERROTA, sessao.estado());
        assertEquals(4, sessao.duracaoSegundos());
    }
}
