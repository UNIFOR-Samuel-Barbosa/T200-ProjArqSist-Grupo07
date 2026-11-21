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
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void movimentoAtualizaPosicaoEExplicaQuandoNaveEstaNoLimite() {
        Nave nave = new Nave(new Posicao(0, 1), 1);
        GameSession sessao = new GameSession(new Missao(nave, new Base(new Posicao(0, 0)),
                5, List.of(new Professor("Mia", new Posicao(2, 2))), List.of(), List.of()),
                new Random(2), "Piloto", "Fácil", 30);

        sessao.processar(Acao.DIREITA);
        assertEquals(new Posicao(1, 1), nave.posicao());
        assertTrue(sessao.processar(Acao.CIMA).mensagem().contains("Nave movida para"));
        assertEquals(new Posicao(1, 0), nave.posicao());

        Nave naveNoLimite = new Nave(new Posicao(5, 0), 1);
        GameSession sessaoNoLimite = new GameSession(new Missao(naveNoLimite, new Base(new Posicao(0, 0)),
                5, List.of(new Professor("Noa", new Posicao(3, 3))), List.of(), List.of()),
                new Random(3), "Piloto", "Fácil", 30);

        ResultadoTurno resultado = sessaoNoLimite.processar(Acao.DIREITA);

        assertEquals(new Posicao(5, 0), naveNoLimite.posicao());
        assertTrue(resultado.mensagem().contains("Limite do mapa atingido"));
    }

    @Test
    void tentativaDeEmbarqueEmCelulaAdjacenteNaoMovePassageiroOuNave() {
        Posicao posicaoPassageiro = new Posicao(1, 1);
        Nave nave = new Nave(new Posicao(0, 1), 1);
        Missao missao = new Missao(nave, new Base(new Posicao(0, 0)), 5,
                List.of(new Professor("Lia", posicaoPassageiro)), List.of(), List.of());
        GameSession sessao = new GameSession(missao, new Random(4), "Piloto", "Fácil", 30);

        ResultadoTurno resultado = sessao.processar(Acao.EMBARCAR);

        assertEquals(posicaoPassageiro, missao.passageiros().get(0).posicao());
        assertEquals(new Posicao(0, 1), nave.posicao());
        assertTrue(resultado.mensagem().contains("Nenhum passageiro na posição"));
    }
}
