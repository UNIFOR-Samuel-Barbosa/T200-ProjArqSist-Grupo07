package br.edu.unifor.missaomarte.gdx.rede;

import br.edu.unifor.missaomarte.application.servidor.LinhaDoRanking;
import br.edu.unifor.missaomarte.application.servidor.PartidaConcluida;
import br.edu.unifor.missaomarte.application.servidor.PartidaRegistrada;
import br.edu.unifor.missaomarte.application.servidor.Resposta;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Conversão de JSON e mensagens de erro, sem rede, janela ou OpenGL. */
class JsonDoServidorTest {
    @Test
    void credenciaisViramJsonValidoMesmoComAspasEBarrasNaSenha() {
        String json = JsonDoServidor.credenciais("Lia", "a\"b\\c");

        JsonValue lido = new JsonReader().parse(json);
        assertEquals("Lia", lido.getString("nome"));
        assertEquals("a\"b\\c", lido.getString("senha"));
        assertEquals(2, lido.size);
    }

    @Test
    void partidaTemNumerosSemAspasInclusiveNegativos() {
        String json = JsonDoServidor.partida(new PartidaConcluida("DIFICIL", "DERROTA", -3, 21, 33));

        assertTrue(json.contains("\"pontuacao\":-3"), json);
        assertTrue(json.contains("\"duracaoSegundos\":21"), json);
        assertTrue(json.contains("\"dificuldade\":\"DIFICIL\""), json);
        assertFalse(json.contains("usuario"));
    }

    @Test
    void leTokenEFalhaSeOCampoFaltar() {
        assertEquals("abc.def.ghi", JsonDoServidor.token("{\"token\":\"abc.def.ghi\",\"tipo\":\"Bearer\"}"));
        assertThrows(IllegalArgumentException.class, () -> JsonDoServidor.token("{\"tipo\":\"Bearer\"}"));
    }

    @Test
    void leRankingEListaVazia() {
        List<LinhaDoRanking> ranking = JsonDoServidor.ranking("[{\"posicao\":1,\"nome\":\"Lia\",\"pontuacao\":47,"
                + "\"dificuldade\":\"FACIL\",\"duracaoSegundos\":21,\"jogadaEm\":\"2026-11-20T23:04:00Z\"}]");

        assertEquals(List.of(new LinhaDoRanking(1, "Lia", 47, "FACIL", 21)), ranking);
        assertTrue(JsonDoServidor.ranking("[]").isEmpty());
    }

    @Test
    void leHistoricoEListaVazia() {
        List<PartidaRegistrada> historico = JsonDoServidor.historico("[{\"id\":7,\"dificuldade\":\"MEDIO\","
                + "\"resultado\":\"DERROTA\",\"pontuacao\":-2,\"duracaoSegundos\":40,\"movimentos\":12,"
                + "\"jogadaEm\":\"2026-11-20T23:04:00.916877Z\"}]");

        assertEquals(1, historico.size());
        assertEquals(new PartidaRegistrada(7, "MEDIO", "DERROTA", -2, 40, 12,
                Instant.parse("2026-11-20T23:04:00.916877Z")), historico.get(0));
        assertTrue(JsonDoServidor.historico("[]").isEmpty());
    }

    @Test
    void respostaDeSucessoComCorpoEstranhoViraErroENaoExcecao() {
        Resposta<String> resposta = JsonDoServidor.interpretar(200, "<html>", true, JsonDoServidor::token);

        assertFalse(resposta.sucesso());
        assertEquals(JsonDoServidor.RESPOSTA_INVALIDA, resposta.erro());
        assertFalse(JsonDoServidor.interpretar(200, "{}", false, JsonDoServidor::ranking).sucesso());
    }

    @Test
    void statusSemRespostaDoServidorViraMensagemDeConexao() {
        Resposta<Object> resposta = JsonDoServidor.interpretar(-1, null, true, corpo -> corpo);

        assertEquals("Não foi possível conectar ao servidor.", resposta.erro());
        assertFalse(resposta.naoAutorizado());
    }

    @Test
    void naoAutorizadoSoEmRotaProtegida() {
        Resposta<Object> protegida = JsonDoServidor.interpretar(401, "", true, corpo -> corpo);
        Resposta<Object> login = JsonDoServidor.interpretar(401,
                "{\"detail\":\"Usuário ou senha inválidos.\",\"status\":401}", false, corpo -> corpo);

        assertTrue(protegida.naoAutorizado());
        assertFalse(login.naoAutorizado());
        assertEquals("Usuário ou senha inválidos.", login.erro());
    }

    @Test
    void mensagemDeErroUsaErroDeCampoDepoisDetailDepoisTextoPadrao() {
        assertEquals("A senha precisa de pelo menos 8 caracteres.", JsonDoServidor.mensagemDeErro(400,
                "{\"detail\":\"Dados inválidos.\",\"erros\":{\"senha\":\"A senha precisa de pelo menos 8 caracteres.\"}}"));
        assertEquals("Este nome já está em uso.", JsonDoServidor.mensagemDeErro(409,
                "{\"detail\":\"Este nome já está em uso.\",\"status\":409}"));
        assertEquals("Usuário ou senha inválidos.", JsonDoServidor.mensagemDeErro(401, ""));
        assertTrue(JsonDoServidor.mensagemDeErro(503, "").contains("servidor teve um problema"));
        assertTrue(JsonDoServidor.mensagemDeErro(418, "").contains("418"));
    }

    @Test
    void corpoQueNaoEJsonNuncaVazaParaOJogador() {
        String corpo = "<html>java.lang.NullPointerException at Segredo.metodo</html>";

        String mensagem = JsonDoServidor.mensagemDeErro(500, corpo);

        assertFalse(mensagem.contains("Exception"));
        assertFalse(mensagem.contains("html"));
    }
}
