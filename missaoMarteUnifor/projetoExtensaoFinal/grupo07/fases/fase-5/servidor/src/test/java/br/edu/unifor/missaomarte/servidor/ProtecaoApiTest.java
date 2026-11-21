package br.edu.unifor.missaomarte.servidor;

import br.edu.unifor.missaomarte.servidor.persistencia.UsuarioRepository;
import br.edu.unifor.missaomarte.servidor.seguranca.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProtecaoApiTest extends ApiTeste {
    @Autowired
    TokenService tokens;
    @Autowired
    UsuarioRepository usuarios;

    @Test
    void semTokenDa401EmTodasAsRotasProtegidas() throws Exception {
        mvc.perform(get("/api/ranking")).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Token ausente, inválido ou expirado."));
        mvc.perform(get("/api/partidas/minhas")).andExpect(status().isUnauthorized());
        postJson("/api/partidas", "{}").andExpect(status().isUnauthorized());
        mvc.perform(get("/qualquer-outra-rota")).andExpect(status().isUnauthorized());
    }

    @Test
    void tokenInvalidoOuAdulteradoDa401() throws Exception {
        getAutenticado("/api/ranking", "Bearer nao.e.um-token").andExpect(status().isUnauthorized());
        String valido = jogador("Lia");
        getAutenticado("/api/ranking", valido.substring(0, valido.length() - 2) + "xx").andExpect(status().isUnauthorized());
    }

    @Test
    void tokenExpiradoDa401() throws Exception {
        jogador("Lia");
        Instant passado = Instant.now().minus(Duration.ofHours(3));
        String expirado = tokens.emitir(usuarios.findByNomeNormalizado("lia").orElseThrow(),
                passado, passado.plus(Duration.ofHours(2))).token();

        getAutenticado("/api/ranking", "Bearer " + expirado).andExpect(status().isUnauthorized());
    }

    @Test
    void tokenValidoDa200() throws Exception {
        getAutenticado("/api/ranking", jogador("Lia")).andExpect(status().isOk());
    }
}
