package br.edu.unifor.missaomarte.application.servidor;

/** Uma linha do ranking global: a melhor vitória de um jogador. */
public record LinhaDoRanking(int posicao, String nome, int pontuacao, String dificuldade, long duracaoSegundos) {
}
