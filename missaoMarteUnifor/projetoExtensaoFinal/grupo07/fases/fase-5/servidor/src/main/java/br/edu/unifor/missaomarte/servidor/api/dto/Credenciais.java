package br.edu.unifor.missaomarte.servidor.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Corpo de cadastro e de login. A senha nunca aparece no {@code toString} (logs, depuração).
 * BCrypt só lê os 72 primeiros bytes: senhas maiores seriam cortadas em silêncio, por isso o limite.
 */
public record Credenciais(
        @NotNull(message = "Informe o nome.")
        @Pattern(regexp = "[A-Za-z0-9_-]{3,20}", message = "Use de 3 a 20 letras, números, _ ou -.")
        String nome,
        @NotNull(message = "Informe a senha.")
        @Size(min = 8, message = "A senha precisa de pelo menos 8 caracteres.")
        @AteBytes(72)
        String senha) {

    @Override
    public String toString() {
        return "Credenciais[nome=" + nome + ", senha=***]";
    }
}
