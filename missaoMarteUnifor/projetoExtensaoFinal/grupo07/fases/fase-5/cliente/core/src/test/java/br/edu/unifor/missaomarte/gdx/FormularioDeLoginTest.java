package br.edu.unifor.missaomarte.gdx;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FormularioDeLoginTest {
    private static FormularioDeLogin preenchido(String nome, String senha) {
        FormularioDeLogin f = new FormularioDeLogin();
        nome.chars().forEach(c -> f.digitar((char) c));
        f.alternarCampo();
        senha.chars().forEach(c -> f.digitar((char) c));
        return f;
    }

    @Test
    void nomeAceitaLetrasNumerosSublinhadoEHifenERejeitaOResto() {
        FormularioDeLogin f = new FormularioDeLogin();

        assertTrue(f.digitar('a'));
        assertTrue(f.digitar('Z'));
        assertTrue(f.digitar('7'));
        assertTrue(f.digitar('_'));
        assertTrue(f.digitar('-'));
        assertFalse(f.digitar(' '));
        assertFalse(f.digitar('é'));
        assertFalse(f.digitar('"'));
        assertEquals("aZ7_-", f.nome());
    }

    @Test
    void nomeParaEmVinteCaracteres() {
        FormularioDeLogin f = preenchido("a".repeat(25), "");

        assertEquals(20, f.nome().length());
    }

    @Test
    void senhaAceitaQualquerCaractereVisivelAte72BytesEApareceMascarada() {
        FormularioDeLogin f = preenchido("", "a\"b\\c é!");
        assertEquals("a\"b\\c é!", f.senha());
        assertEquals("********", f.senhaMascarada());
        assertFalse(f.digitar('\t'));

        FormularioDeLogin longa = preenchido("", "é".repeat(40));
        assertEquals(36, longa.senha().length(), "36 × 2 bytes = 72 bytes");
    }

    @Test
    void apagarAgeSoNoCampoAtivo() {
        FormularioDeLogin f = preenchido("Lia", "123");

        f.apagar();
        assertEquals("12", f.senha());
        f.alternarCampo();
        f.apagar();
        assertEquals("Li", f.nome());
        assertEquals(FormularioDeLogin.Campo.NOME, f.campo());
    }

    @Test
    void validacaoDeEntrarECriarConta() {
        assertEquals("O nome precisa de 3 a 20 letras, números, _ ou -.", preenchido("Li", "12345678").validar());
        assertEquals("Informe a senha.", preenchido("Lia", "").validar());
        assertNull(preenchido("Lia", "123").validar(), "ao entrar, quem decide a senha é o servidor");

        FormularioDeLogin criar = preenchido("Lia", "1234567");
        criar.alternarModo();
        assertEquals(FormularioDeLogin.Modo.CRIAR_CONTA, criar.modo());
        assertEquals("A senha precisa de pelo menos 8 caracteres.", criar.validar());
        criar.digitar('8');
        assertNull(criar.validar());
    }

    @Test
    void senhaNaoApareceNoToStringESomeAoLimpar() {
        FormularioDeLogin f = preenchido("Lia", "segredo123");

        assertFalse(f.toString().contains("segredo123"));
        f.limparSenha();
        assertEquals("", f.senha());
    }
}
