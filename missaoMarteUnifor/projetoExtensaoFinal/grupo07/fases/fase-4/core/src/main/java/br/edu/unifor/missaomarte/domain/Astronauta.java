package br.edu.unifor.missaomarte.domain;

public final class Astronauta extends Passageiro {
    public Astronauta(String nome, Posicao posicao) {
        super(nome, posicao, 20);
    }

    @Override
    public char simbolo() {
        return 'T';
    }
}
