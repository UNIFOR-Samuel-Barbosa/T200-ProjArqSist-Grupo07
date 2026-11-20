package br.edu.unifor.missaomarte.servidor;

import br.edu.unifor.missaomarte.servidor.persistencia.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PartidaApiTest extends ApiTeste {
    @Autowired
    UsuarioRepository usuarios;

    @Test
    void registraPartidaDoDonoDoToken() throws Exception {
        enviarPartida(jogador("Lia"), "VITORIA", 47, 21).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.dificuldade").value("FACIL"))
                .andExpect(jsonPath("$.resultado").value("VITORIA"))
                .andExpect(jsonPath("$.pontuacao").value(47))
                .andExpect(jsonPath("$.duracaoSegundos").value(21))
                .andExpect(jsonPath("$.jogadaEm").exists());
    }

    @Test
    void campoUsuarioNoCorpoEIgnorado() throws Exception {
        String lia = jogador("Lia");
        String caio = jogador("Caio");
        long idCaio = usuarios.findByNomeNormalizado("caio").orElseThrow().getId();

        enviarPartida(lia, "{\"usuario\":" + idCaio + ",\"usuarioId\":" + idCaio + ",\"dificuldade\":\"MEDIO\","
                + "\"resultado\":\"DERROTA\",\"pontuacao\":-3,\"duracaoSegundos\":10,\"movimentos\":5}")
                .andExpect(status().isCreated());

        getAutenticado("/api/partidas/minhas", lia).andExpect(jsonPath("$", hasSize(1)));
        getAutenticado("/api/partidas/minhas", caio).andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void enumInvalidoValorForaDaFaixaECampoAusenteDao400() throws Exception {
        String lia = jogador("Lia");
        enviarPartida(lia, "{\"dificuldade\":\"IMPOSSIVEL\",\"resultado\":\"VITORIA\",\"pontuacao\":1,"
                + "\"duracaoSegundos\":1,\"movimentos\":1}").andExpect(status().isBadRequest());
        enviarPartida(lia, "{\"dificuldade\":\"FACIL\",\"resultado\":\"ABORTADA\",\"pontuacao\":1,"
                + "\"duracaoSegundos\":1,\"movimentos\":1}").andExpect(status().isBadRequest());
        enviarPartida(lia, "VITORIA", 100_001, 10).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.pontuacao").exists());
        enviarPartida(lia, "VITORIA", 10, 86_401).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.duracaoSegundos").exists());
        enviarPartida(lia, "{\"dificuldade\":\"FACIL\",\"resultado\":\"VITORIA\",\"duracaoSegundos\":1,"
                + "\"movimentos\":1}").andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.pontuacao").value("Informe a pontuação."));
    }

    @Test
    void historicoSoDoDonoDaMaisRecenteParaAMaisAntigaEPaginado() throws Exception {
        String lia = jogador("Lia");
        String caio = jogador("Caio");
        enviarPartida(lia, "VITORIA", 10, 30);
        enviarPartida(caio, "VITORIA", 99, 30);
        enviarPartida(lia, "DERROTA", 0, 40);
        enviarPartida(lia, "VITORIA", 30, 50);

        getAutenticado("/api/partidas/minhas", lia).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].pontuacao").value(30))
                .andExpect(jsonPath("$[1].resultado").value("DERROTA"))
                .andExpect(jsonPath("$[2].pontuacao").value(10));
        getAutenticado("/api/partidas/minhas?pagina=0&tamanho=2", lia).andExpect(jsonPath("$", hasSize(2)));
        getAutenticado("/api/partidas/minhas?pagina=1&tamanho=2", lia).andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].pontuacao").value(10));
        getAutenticado("/api/partidas/minhas?tamanho=51", lia).andExpect(status().isBadRequest());
    }
}
