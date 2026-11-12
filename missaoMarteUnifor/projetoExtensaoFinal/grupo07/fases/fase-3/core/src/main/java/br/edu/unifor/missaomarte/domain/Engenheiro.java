package br.edu.unifor.missaomarte.domain;

public final class Engenheiro extends Passageiro {
    public Engenheiro(String nome, Posicao posicao) {
        super(nome, posicao, 15);
    }

    @Override
    public char simbolo() {
        return 'E';
    }
}
