package br.edu.unifor.missaomarte.application.servidor;

/**
 * Resultado de uma chamada ao servidor: um valor OU uma mensagem de erro já pronta para o jogador.
 * {@code naoAutorizado} marca o 401 em rota protegida (sessão expirada).
 */
public record Resposta<T>(T valor, String erro, boolean naoAutorizado) {
    public static <T> Resposta<T> ok(T valor) {
        return new Resposta<>(valor, null, false);
    }

    public static <T> Resposta<T> falha(String erro) {
        return new Resposta<>(null, erro, false);
    }

    public static <T> Resposta<T> naoAutorizado(String erro) {
        return new Resposta<>(null, erro, true);
    }

    public boolean sucesso() {
        return erro == null;
    }
}
