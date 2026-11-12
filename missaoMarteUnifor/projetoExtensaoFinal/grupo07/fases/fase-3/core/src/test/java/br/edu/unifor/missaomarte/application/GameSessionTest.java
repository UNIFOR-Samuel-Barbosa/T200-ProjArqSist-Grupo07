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

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameSessionTest {
    @Test
    void embarcaEntregaESoEntaoConcluiMissao() {
        Passageiro passageiro = new Professor("Lia", new Posicao(1, 1));
        Missao missao = new Missao(new Nave(new Posicao(0, 1), 1), new Base(new Posicao(0, 0)),
                5, List.of(passageiro), List.of(), List.of());
        GameSession sessao = new GameSession(missao, new Random(4), "Piloto", "Fácil", 30);

        sessao.processar(Acao.DIREITA);
        sessao.processar(Acao.EMBARCAR);
        assertEquals(EstadoPartida.EM_ANDAMENTO, sessao.estado());
        sessao.processar(Acao.ESQUERDA);
        sessao.processar(Acao.CIMA);

        assertEquals(EstadoPartida.VITORIA, sessao.estado());
        assertEquals(1, sessao.passageirosEntregues());
        assertEquals(0, missao.nave().passageiros().size());
    }

    @Test
    void colisaoDiminuiVidaEPontuacao() {
        Missao missao = new Missao(new Nave(new Posicao(0, 1), 1), new Base(new Posicao(0, 0)),
                5, List.of(new Professor("Noa", new Posicao(2, 2))),
                List.of(new Asteroide(new Posicao(1, 1))), List.of());
        GameSession sessao = new GameSession(missao, new Random(1), "Piloto", "Fácil", 30);

        sessao.processar(Acao.DIREITA);

        assertEquals(2, missao.nave().vidas());
        assertEquals(24, sessao.pontuacao());
    }

    @Test
    void entradaInvalidaNaoConsomeTurno() {
        GameSession sessao = new GameSession(new Missao(new Nave(new Posicao(0, 1), 1),
                new Base(new Posicao(0, 0)), 5, List.of(), List.of(), List.of()),
                new Random(1), "Piloto", "Fácil", 30);

        assertEquals(false, sessao.processar(Acao.INVALIDA).consumiuTurno());
        assertEquals(0, sessao.movimentos());
    }
}
