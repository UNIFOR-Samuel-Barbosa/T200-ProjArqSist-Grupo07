package br.edu.unifor.missaomarte.domain;

public record Asteroide(Posicao posicao) implements ObjetoRenderizavel {
    @Override
    public char simbolo() {
        return '#';
    }
}
