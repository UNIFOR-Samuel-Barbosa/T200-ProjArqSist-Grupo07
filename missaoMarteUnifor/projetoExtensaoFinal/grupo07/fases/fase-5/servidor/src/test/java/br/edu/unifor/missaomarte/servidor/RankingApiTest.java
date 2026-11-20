package br.edu.unifor.missaomarte.servidor;

import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RankingApiTest extends ApiTeste {
    @Test
    void semPartidasORankingEVazio() throws Exception {
        getAutenticado("/api/ranking", jogador("Lia")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void umaLinhaPorJogadorComAMelhorVitoria() throws Exception {
        String lia = jogador("Lia");
        String caio = jogador("Caio");
        enviarPartida(lia, "VITORIA", 20, 30);
        enviarPartida(lia, "VITORIA", 50, 30);
        enviarPartida(caio, "VITORIA", 40, 30);

        getAutenticado("/api/ranking", lia).andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].posicao").value(1))
                .andExpect(jsonPath("$[0].nome").value("Lia"))
                .andExpect(jsonPath("$[0].pontuacao").value(50))
                .andExpect(jsonPath("$[1].nome").value("Caio"));
    }

    @Test
    void derrotasEPontuacaoNaoPositivaFicamFora() throws Exception {
        String lia = jogador("Lia");
        String caio = jogador("Caio");
        enviarPartida(lia, "DERROTA", 90, 30);
        enviarPartida(caio, "VITORIA", 0, 30);
        enviarPartida(caio, "VITORIA", -5, 30);

        getAutenticado("/api/ranking", lia).andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void empateDesempataPelaMenorDuracaoEDepoisPelaMaisAntiga() throws Exception {
        String lento = jogador("Lento");
        String rapido = jogador("Rapido");
        String antigo = jogador("Antigo");
        String novo = jogador("Novo");
        enviarPartida(lento, "VITORIA", 40, 90);
        enviarPartida(antigo, "VITORIA", 40, 20);
        enviarPartida(rapido, "VITORIA", 40, 10);
        enviarPartida(novo, "VITORIA", 40, 20);

        getAutenticado("/api/ranking", lento)
                .andExpect(jsonPath("$[0].nome").value("Rapido"))
                .andExpect(jsonPath("$[1].nome").value("Antigo"))
                .andExpect(jsonPath("$[2].nome").value("Novo"))
                .andExpect(jsonPath("$[3].nome").value("Lento"));
    }

    @Test
    void noMaximoDezLinhasSemDadosSensiveis() throws Exception {
        String ultimo = null;
        for (int i = 1; i <= 12; i++) {
            ultimo = jogador("Piloto" + i);
            enviarPartida(ultimo, "VITORIA", i, 30);
        }

        getAutenticado("/api/ranking", ultimo).andExpect(jsonPath("$", hasSize(10)))
                .andExpect(jsonPath("$[0].pontuacao").value(12))
                .andExpect(jsonPath("$[9].pontuacao").value(3))
                .andExpect(content().string(not(containsString("senha"))))
                .andExpect(content().string(not(containsString("$2a$"))))
                .andExpect(content().string(not(containsString("normalizado"))));
    }
}
