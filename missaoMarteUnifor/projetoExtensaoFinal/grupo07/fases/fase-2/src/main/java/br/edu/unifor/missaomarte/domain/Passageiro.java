package br.edu.unifor.missaomarte.domain;

public abstract class Passageiro implements ObjetoRenderizavel {
    private final String nome;
    private final Posicao posicao;
    private final int valorResgate;

    protected Passageiro(String nome, Posicao posicao, int valorResgate) {
        this.nome = nome;
        this.posicao = posicao;
        this.valorResgate = valorResgate;
    }

    public String nome() {
        return nome;
    }

    public int valorResgate() {
        return valorResgate;
    }

    @Override
    public Posicao posicao() {
        return posicao;
    }
}
