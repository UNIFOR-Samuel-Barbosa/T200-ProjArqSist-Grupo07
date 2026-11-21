package br.edu.unifor.missaomarte.gdx.visual;

import br.edu.unifor.missaomarte.gdx.recursos.Imagem;

/**
 * Animação de uma execução (explosão, brilho) em uma célula. Guarda o próprio tempo, distribui os
 * quadros igualmente pela duração e avisa quando terminou, para ser removida da lista.
 */
public final class EfeitoUnico {
    private final Imagem imagem;
    private final float x;
    private final float y;
    private final float duracao;
    private float tempo;

    public EfeitoUnico(Imagem imagem, float x, float y, float duracao) {
        this.imagem = imagem;
        this.x = x;
        this.y = y;
        this.duracao = duracao;
    }

    /** Avança o tempo e devolve {@code true} enquanto o efeito continua ativo. */
    public boolean avancar(float delta) {
        tempo += delta;
        return tempo < duracao;
    }

    public int quadroAtual() {
        int quadro = (int) (tempo / duracao * imagem.quadros());
        return Math.min(quadro, imagem.quadros() - 1);
    }

    public Imagem imagem() {
        return imagem;
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }
}
