package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.Acao;
import com.badlogic.gdx.Input;

public final class TecladoParaAcao {
    private TecladoParaAcao() {
    }

    public static Acao converter(int tecla) {
        return switch (tecla) {
            case Input.Keys.W, Input.Keys.UP -> Acao.CIMA;
            case Input.Keys.S, Input.Keys.DOWN -> Acao.BAIXO;
            case Input.Keys.A, Input.Keys.LEFT -> Acao.ESQUERDA;
            case Input.Keys.D, Input.Keys.RIGHT -> Acao.DIREITA;
            case Input.Keys.E -> Acao.EMBARCAR;
            case Input.Keys.Q, Input.Keys.ESCAPE -> Acao.ABORTAR;
            default -> Acao.INVALIDA;
        };
    }
}
