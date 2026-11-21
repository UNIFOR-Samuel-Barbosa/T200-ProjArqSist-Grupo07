package br.edu.unifor.missaomarte.application.servidor;

import java.time.Instant;

/** Partida guardada no servidor (resposta do envio e itens do histórico). */
public record PartidaRegistrada(long id, String dificuldade, String resultado, int pontuacao, long duracaoSegundos,
                                int movimentos, Instant jogadaEm) {
}
