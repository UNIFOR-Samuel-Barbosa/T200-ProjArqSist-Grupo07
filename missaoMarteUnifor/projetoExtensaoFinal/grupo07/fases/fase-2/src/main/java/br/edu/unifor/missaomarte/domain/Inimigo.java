package br.edu.unifor.missaomarte.domain;

import java.util.Random;
import java.util.function.Predicate;

public final class Inimigo implements ObjetoRenderizavel {
    private Posicao posicao;

    public Inimigo(Posicao posicao) {
        this.posicao = posicao;
    }

    @Override
    public Posicao posicao() {
        return posicao;
    }

    @Override
    public char simbolo() {
        return 'X';
    }

    public void mover(Random random, int limite, Predicate<Posicao> posicaoPermitida) {
        int direcao = random.nextInt(4);
        int dx = direcao == 0 ? 1 : direcao == 1 ? -1 : 0;
        int dy = direcao == 2 ? 1 : direcao == 3 ? -1 : 0;
        Posicao proxima = posicao.deslocar(dx, dy);
        if (Math.abs(proxima.x()) <= limite && Math.abs(proxima.y()) <= limite
                && posicaoPermitida.test(proxima)) {
            posicao = proxima;
        }
    }
}
