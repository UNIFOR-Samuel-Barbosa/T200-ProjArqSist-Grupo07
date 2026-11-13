package br.edu.unifor.missaomarte.domain;

public record Base(Posicao posicao) implements ObjetoRenderizavel {
    @Override
    public char simbolo() {
        return 'L';
    }
}
