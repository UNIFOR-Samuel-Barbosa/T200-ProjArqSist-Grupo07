package br.edu.unifor.missaomarte.application.servidor;

import br.edu.unifor.missaomarte.application.Acao;
import br.edu.unifor.missaomarte.application.GameSession;
import br.edu.unifor.missaomarte.domain.Asteroide;
import br.edu.unifor.missaomarte.domain.Base;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.Nave;
import br.edu.unifor.missaomarte.domain.Posicao;
import br.edu.unifor.missaomarte.domain.Professor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DadosDoServidorTest {
    private final AtomicLong relogio = new AtomicLong();

    private GameSession sessao(int pontos, List<Asteroide> asteroides) {
        Missao missao = new Missao(new Nave(new Posicao(1, 0), 1), new Base(new Posicao(0, 0)), 5,
                List.of(new Professor("Lia", new Posicao(1, 0))), asteroides, List.of());
        return new GameSession(missao, new Random(1), "Lia", "Fácil", pontos, relogio::get);
    }

    @Test
    void partidaConcluidaDeUmaVitoria() {
        GameSession sessao = sessao(30, List.of());
        sessao.processar(Acao.EMBARCAR);
        relogio.set(21_000);
        sessao.processar(Acao.ESQUERDA);

        PartidaConcluida partida = PartidaConcluida.de(sessao, Dificuldade.FACIL);

        assertEquals(new PartidaConcluida("FACIL", "VITORIA", 39, 21, 1), partida);
        assertTrue(partida.vitoria());
    }

    @Test
    void partidaConcluidaDeUmaDerrota() {
        GameSession sessao = sessao(6, List.of(new Asteroide(new Posicao(1, 1))));
        sessao.processar(Acao.BAIXO);

        assertEquals("DERROTA", PartidaConcluida.de(sessao, Dificuldade.DIFICIL).resultado());
    }

    @Test
    void partidaEmAndamentoOuAbortadaNaoPodeSerEnviada() {
        GameSession sessao = sessao(30, List.of());
        assertThrows(IllegalStateException.class, () -> PartidaConcluida.de(sessao, Dificuldade.FACIL));

        sessao.processar(Acao.ABORTAR);
        assertThrows(IllegalStateException.class, () -> PartidaConcluida.de(sessao, Dificuldade.FACIL));
    }

    @Test
    void sessaoNaoMostraOTokenNoToString() {
        Sessao sessao = new Sessao("Lia", "eyJhbGciOiJIUzI1NiJ9.segredo");

        assertFalse(sessao.toString().contains("eyJ"));
        assertTrue(sessao.toString().contains("Lia"));
    }

    @Test
    void osTresTiposDeResposta() {
        Resposta<String> ok = Resposta.ok("valor");
        Resposta<String> falha = Resposta.falha("Não foi possível conectar ao servidor.");
        Resposta<String> expirada = Resposta.naoAutorizado("Sua sessão expirou.");

        assertTrue(ok.sucesso());
        assertEquals("valor", ok.valor());
        assertFalse(falha.sucesso());
        assertFalse(falha.naoAutorizado());
        assertNull(falha.valor());
        assertFalse(expirada.sucesso());
        assertTrue(expirada.naoAutorizado());
    }
}
