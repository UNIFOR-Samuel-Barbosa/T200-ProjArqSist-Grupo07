package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.Acao;

/**
 * Repetição ao segurar uma tecla de movimento: espera um atraso e depois repete a cada intervalo.
 * Cada repetição é um turno completo. Sem libGDX: a tela informa o tempo e as teclas.
 */
public final class RepetidorDeTecla {
    public static final float ATRASO = 0.28f;
    public static final float INTERVALO = 0.14f;

    private Acao segurada;
    private float espera;

    public void pressionar(Acao acao) {
        if (acao.movimento()) {
            segurada = acao;
            espera = ATRASO;
        }
    }

    public void soltar(Acao acao) {
        if (acao == segurada) {
            parar();
        }
    }

    public void parar() {
        segurada = null;
    }

    /** Devolve a ação a repetir neste quadro ou {@code null}; no máximo uma repetição por chamada. */
    public Acao avancar(float delta) {
        if (segurada == null) {
            return null;
        }
        espera -= delta;
        if (espera > 0) {
            return null;
        }
        espera = Math.max(espera + INTERVALO, 0.0001f);
        return segurada;
    }
}
