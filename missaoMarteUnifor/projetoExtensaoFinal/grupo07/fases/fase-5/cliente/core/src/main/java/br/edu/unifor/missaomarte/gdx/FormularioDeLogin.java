package br.edu.unifor.missaomarte.gdx;

import java.nio.charset.StandardCharsets;

/**
 * Estado do formulário de entrar/criar conta, sem libGDX: campos, campo ativo, modo, caracteres aceitos,
 * mascaramento e validação. Valida o mesmo que o servidor para avisar cedo, mas só o servidor é confiável.
 */
final class FormularioDeLogin {
    static final int NOME_MAXIMO = 20;
    static final int SENHA_MINIMA = 8;
    static final int SENHA_MAXIMA_BYTES = 72;

    enum Campo { NOME, SENHA }

    enum Modo { ENTRAR, CRIAR_CONTA }

    private final StringBuilder nome = new StringBuilder();
    private final StringBuilder senha = new StringBuilder();
    private Campo campo = Campo.NOME;
    private Modo modo = Modo.ENTRAR;

    /** Digita no campo ativo; devolve se o caractere foi aceito. */
    boolean digitar(char c) {
        if (campo == Campo.NOME) {
            if (nome.length() < NOME_MAXIMO && (Character.isLetterOrDigit(c) && c < 128 || c == '_' || c == '-')) {
                nome.append(c);
                return true;
            }
            return false;
        }
        if (Character.isISOControl(c) || bytes(senha + String.valueOf(c)) > SENHA_MAXIMA_BYTES) {
            return false;
        }
        senha.append(c);
        return true;
    }

    void apagar() {
        StringBuilder alvo = campo == Campo.NOME ? nome : senha;
        if (!alvo.isEmpty()) {
            alvo.deleteCharAt(alvo.length() - 1);
        }
    }

    void alternarCampo() {
        campo = campo == Campo.NOME ? Campo.SENHA : Campo.NOME;
    }

    void alternarModo() {
        modo = modo == Modo.ENTRAR ? Modo.CRIAR_CONTA : Modo.ENTRAR;
    }

    /** Mensagem do primeiro problema, ou {@code null} se pode enviar. */
    String validar() {
        if (nome.length() < 3) {
            return "O nome precisa de 3 a 20 letras, números, _ ou -.";
        }
        if (senha.isEmpty()) {
            return "Informe a senha.";
        }
        if (modo == Modo.CRIAR_CONTA && senha.length() < SENHA_MINIMA) {
            return "A senha precisa de pelo menos 8 caracteres.";
        }
        return null;
    }

    String senhaMascarada() {
        return "*".repeat(senha.length());
    }

    String nome() {
        return nome.toString();
    }

    String senha() {
        return senha.toString();
    }

    Campo campo() {
        return campo;
    }

    Modo modo() {
        return modo;
    }

    /** Ao sair da tela, a senha não fica na memória do formulário. */
    void limparSenha() {
        senha.setLength(0);
    }

    private static int bytes(String texto) {
        return texto.getBytes(StandardCharsets.UTF_8).length;
    }

    @Override
    public String toString() {
        return "FormularioDeLogin[nome=" + nome + ", senha=***, modo=" + modo + "]";
    }
}
