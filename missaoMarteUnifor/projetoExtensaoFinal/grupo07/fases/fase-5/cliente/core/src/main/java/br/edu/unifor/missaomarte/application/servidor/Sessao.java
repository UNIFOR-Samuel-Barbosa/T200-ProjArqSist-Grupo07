package br.edu.unifor.missaomarte.application.servidor;

/** Jogador logado. Fica só na memória (nunca em arquivo) e o {@code toString} esconde o token. */
public record Sessao(String nome, String token) {
    @Override
    public String toString() {
        return "Sessao[nome=" + nome + ", token=***]";
    }
}
