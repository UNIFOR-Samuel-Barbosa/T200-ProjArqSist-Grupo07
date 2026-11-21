package br.edu.unifor.missaomarte.servidor.servico;

/** Mesma mensagem para usuário inexistente e senha errada: não revela quais nomes existem. */
public class CredenciaisInvalidasException extends RuntimeException {
    public CredenciaisInvalidasException() {
        super("Usuário ou senha inválidos.");
    }
}
