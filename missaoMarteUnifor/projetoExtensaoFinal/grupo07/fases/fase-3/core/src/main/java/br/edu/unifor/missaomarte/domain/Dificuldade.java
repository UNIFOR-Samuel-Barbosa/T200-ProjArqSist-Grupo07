package br.edu.unifor.missaomarte.domain;

import java.util.Locale;
import java.util.Optional;

public enum Dificuldade {
    FACIL("Fácil", 4, 1, 1, 30),
    MEDIO("Médio", 5, 2, 2, 20),
    DIFICIL("Difícil", 5, 3, 3, 45);

    private final String rotulo;
    private final int quantidadePassageiros;
    private final int quantidadeAsteroides;
    private final int quantidadeInimigos;
    private final int pontuacaoInicial;

    Dificuldade(String rotulo, int passageiros, int asteroides, int inimigos, int pontuacaoInicial) {
        this.rotulo = rotulo;
        this.quantidadePassageiros = passageiros;
        this.quantidadeAsteroides = asteroides;
        this.quantidadeInimigos = inimigos;
        this.pontuacaoInicial = pontuacaoInicial;
    }

    public int quantidadePassageiros() {
        return quantidadePassageiros;
    }

    public int quantidadeAsteroides() {
        return quantidadeAsteroides;
    }

    public int quantidadeInimigos() {
        return quantidadeInimigos;
    }

    public int pontuacaoInicial() {
        return pontuacaoInicial;
    }

    @Override
    public String toString() {
        return rotulo;
    }

    public static Optional<Dificuldade> tentarInterpretar(String texto) {
        if (texto == null) {
            return Optional.empty();
        }
        return switch (texto.trim().toLowerCase(Locale.ROOT)) {
            case "facil", "fácil" -> Optional.of(FACIL);
            case "medio", "médio" -> Optional.of(MEDIO);
            case "dificil", "difícil" -> Optional.of(DIFICIL);
            default -> Optional.empty();
        };
    }
}
