package br.edu.unifor.missaomarte.application.ranking;

import java.time.Instant;

public record RankingEntry(String piloto, int pontuacao, String dificuldade, Instant concluidaEm) {
}
