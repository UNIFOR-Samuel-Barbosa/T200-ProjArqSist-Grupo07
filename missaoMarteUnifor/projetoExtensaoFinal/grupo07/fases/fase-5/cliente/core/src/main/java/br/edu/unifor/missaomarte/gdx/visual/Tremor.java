package br.edu.unifor.missaomarte.gdx.visual;

/** Tremor da tela na colisão: começa forte e decai linearmente até zero. */
public final class Tremor {
    private final float forca;
    private final float duracao;
    private float restante;

    public Tremor(float forcaEmPixels, float duracao) {
        this.forca = forcaEmPixels;
        this.duracao = duracao;
    }

    public void disparar() {
        restante = duracao;
    }

    public void avancar(float delta) {
        restante = Math.max(0f, restante - delta);
    }

    public float intensidade() {
        return forca * restante / duracao;
    }

    /** Deslocamento oscilante determinístico (sem Random, para ser testável). */
    public float deslocamentoX() {
        return intensidade() * (float) Math.sin(restante * 90f);
    }

    public float deslocamentoY() {
        return intensidade() * (float) Math.cos(restante * 70f);
    }
}
