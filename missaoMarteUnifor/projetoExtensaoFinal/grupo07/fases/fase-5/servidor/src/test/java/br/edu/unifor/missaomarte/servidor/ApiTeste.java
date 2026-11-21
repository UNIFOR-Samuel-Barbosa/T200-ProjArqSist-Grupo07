package br.edu.unifor.missaomarte.servidor;

import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Base dos testes de API: contexto completo, H2 em memória e segredo fixo (src/test/resources),
 * e cada teste desfeito ao final (@Transactional). Nada toca o banco real nem a rede.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public abstract class ApiTeste {
    protected static final String SENHA = "senha-de-teste";

    @Autowired
    protected MockMvc mvc;

    protected ResultActions postJson(String rota, String json) throws Exception {
        return mvc.perform(post(rota).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    protected ResultActions cadastrar(String nome, String senha) throws Exception {
        return postJson("/api/usuarios", credenciais(nome, senha));
    }

    protected ResultActions entrar(String nome, String senha) throws Exception {
        return postJson("/api/auth/login", credenciais(nome, senha));
    }

    protected static String credenciais(String nome, String senha) {
        return "{\"nome\":\"" + nome + "\",\"senha\":\"" + senha + "\"}";
    }

    /** Cadastra, entra e devolve o valor pronto do cabeçalho Authorization. */
    protected String jogador(String nome) throws Exception {
        cadastrar(nome, SENHA).andExpect(status().isCreated());
        String corpo = entrar(nome, SENHA).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return "Bearer " + JsonPath.<String>read(corpo, "$.token");
    }

    protected ResultActions enviarPartida(String autorizacao, String json) throws Exception {
        return mvc.perform(post("/api/partidas").header("Authorization", autorizacao)
                .contentType(MediaType.APPLICATION_JSON).content(json));
    }

    protected ResultActions enviarPartida(String autorizacao, String resultado, int pontuacao, long duracao) throws Exception {
        return enviarPartida(autorizacao, "{\"dificuldade\":\"FACIL\",\"resultado\":\"" + resultado
                + "\",\"pontuacao\":" + pontuacao + ",\"duracaoSegundos\":" + duracao + ",\"movimentos\":20}");
    }

    protected ResultActions getAutenticado(String rota, String autorizacao) throws Exception {
        return mvc.perform(get(rota).header("Authorization", autorizacao));
    }
}
