package br.edu.unifor.missaomarte.servidor.api.dto;

import br.edu.unifor.missaomarte.servidor.persistencia.Dificuldade;
import br.edu.unifor.missaomarte.servidor.persistencia.Resultado;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Resultado enviado pelo cliente. Tipos que aceitam {@code null} (Integer, Long) para que um campo
 * ausente vire 400 em vez de zero. Não existe campo de usuário: o dono vem do token, e um campo
 * "usuario" no JSON é simplesmente ignorado. Os limites só barram absurdos; não provam a pontuação.
 */
public record NovaPartida(
        @NotNull(message = "Informe a dificuldade.") Dificuldade dificuldade,
        @NotNull(message = "Informe o resultado.") Resultado resultado,
        @NotNull(message = "Informe a pontuação.")
        @Min(value = -100_000, message = "Pontuação fora da faixa.")
        @Max(value = 100_000, message = "Pontuação fora da faixa.") Integer pontuacao,
        @NotNull(message = "Informe a duração.")
        @Min(value = 0, message = "Duração fora da faixa.")
        @Max(value = 86_400, message = "Duração fora da faixa.") Long duracaoSegundos,
        @NotNull(message = "Informe os movimentos.")
        @Min(value = 0, message = "Movimentos fora da faixa.")
        @Max(value = 100_000, message = "Movimentos fora da faixa.") Integer movimentos) {
}
