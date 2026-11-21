package br.edu.unifor.missaomarte.application;

import java.util.Locale;

public enum Acao {
    CIMA(0, -1),
    BAIXO(0, 1),
    ESQUERDA(-1, 0),
    DIREITA(1, 0),
    EMBARCAR(0, 0),
    ABORTAR(0, 0),
    INVALIDA(0, 0);

    private final int dx;
    private final int dy;

    Acao(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int dx() {
        return dx;
    }

    public int dy() {
        return dy;
    }

    public boolean movimento() {
        return dx != 0 || dy != 0;
    }

    public static Acao interpretar(String comando) {
        if (comando == null || comando.isBlank()) {
            return INVALIDA;
        }
        return switch (comando.trim().toLowerCase(Locale.ROOT)) {
            case "w", "↑" -> CIMA;
            case "s", "↓" -> BAIXO;
            case "a", "←" -> ESQUERDA;
            case "d", "→" -> DIREITA;
            case "e", "c" -> EMBARCAR;
            case "q" -> ABORTAR;
            default -> INVALIDA;
        };
    }
}
