package br.edu.unifor.missaomarte.gdx.visual;

/**
 * Texto que sobe e desaparece ("+15", "-6", "Entregue!"). Coordenadas em células do domínio:
 * como o y do jogo cresce para baixo, subir na tela é diminuir o y.
 */
public final class TextoFlutuante {
    public static final float DURACAO = 1.2f;
    public static final float SUBIDA_EM_CELULAS = 1.2f;

    private final String texto;
    private final float x;
    private final float yInicial;
    private float tempo;

    public TextoFlutuante(String texto, float x, float y) {
        this.texto = texto;
        this.x = x;
        this.yInicial = y;
    }

    /** Texto da variação de pontos; variações de ±1 (um passo) não poluem a tela. */
    public static TextoFlutuante variacaoDePontos(int variacao, float x, float y) {
        if (Math.abs(variacao) <= 1) {
            return null;
        }
        return new TextoFlutuante((variacao > 0 ? "+" : "") + variacao, x, y);
    }

    public boolean avancar(float delta) {
        tempo += delta;
        return tempo < DURACAO;
    }

    private float progresso() {
        return Math.min(1f, tempo / DURACAO);
    }

    public String texto() {
        return texto;
    }

    public float x() {
        return x;
    }

    public float y() {
        return yInicial - progresso() * SUBIDA_EM_CELULAS;
    }

    public float alfa() {
        return 1f - progresso();
    }
}
