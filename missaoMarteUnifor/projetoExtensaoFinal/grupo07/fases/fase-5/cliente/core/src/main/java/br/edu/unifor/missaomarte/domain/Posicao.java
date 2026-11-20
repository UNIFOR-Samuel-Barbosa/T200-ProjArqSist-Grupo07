package br.edu.unifor.missaomarte.domain;

public record Posicao(int x, int y) {
    public Posicao deslocar(int deltaX, int deltaY) {
        return new Posicao(x + deltaX, y + deltaY);
    }
}
