package br.edu.unifor.missaomarte.domain;

public final class Professor extends Passageiro {
    public Professor(String nome, Posicao posicao) {
        super(nome, posicao, 10);
    }

    @Override
    public char simbolo() {
        return 'P';
    }
}
