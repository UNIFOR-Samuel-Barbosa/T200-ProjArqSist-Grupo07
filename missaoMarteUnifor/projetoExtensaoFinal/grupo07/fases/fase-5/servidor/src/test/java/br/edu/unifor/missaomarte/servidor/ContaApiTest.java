package br.edu.unifor.missaomarte.servidor;

import br.edu.unifor.missaomarte.servidor.api.dto.Credenciais;
import br.edu.unifor.missaomarte.servidor.persistencia.Usuario;
import br.edu.unifor.missaomarte.servidor.persistencia.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ContaApiTest extends ApiTeste {
    @Autowired
    UsuarioRepository usuarios;

    @Test
    void cadastroDevolve201SemSenhaEGuardaSoOHashBCrypt() throws Exception {
        cadastrar("Lia", SENHA).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("Lia"))
                .andExpect(content().string(not(containsString(SENHA))))
                .andExpect(content().string(not(containsString("senha"))));

        Usuario salvo = usuarios.findByNomeNormalizado("lia").orElseThrow();
        assertTrue(salvo.getSenhaHash().startsWith("$2a$10$"), "BCrypt com custo 10");
        assertNotEquals(SENHA, salvo.getSenhaHash());
    }

    @Test
    void mesmaSenhaGeraHashesDiferentesPorCausaDoSal() throws Exception {
        cadastrar("Lia", SENHA);
        cadastrar("Caio", SENHA);

        assertNotEquals(usuarios.findByNomeNormalizado("lia").orElseThrow().getSenhaHash(),
                usuarios.findByNomeNormalizado("caio").orElseThrow().getSenhaHash());
    }

    @Test
    void nomeRepetidoComOutraCaixaDa409() throws Exception {
        cadastrar("Lia", SENHA).andExpect(status().isCreated());

        cadastrar("LIA", SENHA).andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Este nome já está em uso."));
    }

    @Test
    void nomeESenhaInvalidosDao400ComOCampo() throws Exception {
        cadastrar("ab", SENHA).andExpect(status().isBadRequest()).andExpect(jsonPath("$.erros.nome").exists());
        cadastrar("com espaco", SENHA).andExpect(status().isBadRequest()).andExpect(jsonPath("$.erros.nome").exists());
        cadastrar("Lia", "curta").andExpect(status().isBadRequest()).andExpect(jsonPath("$.erros.senha").exists());
        postJson("/api/usuarios", "{\"nome\":\"Lia\"}").andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.senha").value("Informe a senha."));
        postJson("/api/usuarios", "{json quebrado").andExpect(status().isBadRequest());
    }

    @Test
    void senhaAte72BytesPassaE73NaoPassa() throws Exception {
        cadastrar("Bento", "é".repeat(36)).andExpect(status().isCreated());

        cadastrar("Nina", "é".repeat(36) + "x").andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.senha").value("A senha pode ter no máximo 72 bytes."));
    }

    @Test
    void loginDevolveTokenBearerComExpiracaoESemSenha() throws Exception {
        cadastrar("Lia", SENHA);

        entrar("Lia", SENHA).andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.expiraEm").exists())
                .andExpect(content().string(not(containsString(SENHA))));
        entrar("lia", SENHA).andExpect(status().isOk());
    }

    @Test
    void senhaErradaEUsuarioInexistenteTemAMesmaResposta() throws Exception {
        cadastrar("Lia", SENHA);

        String senhaErrada = entrar("Lia", "outra-senha-qualquer").andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();
        String inexistente = entrar("Ninguem", "outra-senha-qualquer").andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        assertTrue(senhaErrada.contains("Usuário ou senha inválidos."));
        assertEquals(senhaErrada, inexistente);
    }

    @Test
    void toStringDasCredenciaisNaoMostraASenha() {
        assertFalse(new Credenciais("Lia", SENHA).toString().contains(SENHA));
    }
}
