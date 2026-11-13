package br.edu.unifor.missaomarte.domain;

import java.util.ArrayList;
import java.util.List;

public final class Nave implements ObjetoRenderizavel {
    private Posicao posicao;
    private final int capacidade;
    private final List<Passageiro> passageiros = new ArrayList<>();
    private int vidas = 3;

    public Nave(Posicao inicio, int capacidade) {
        this.posicao = inicio;
        this.capacidade = capacidade;
    }

    @Override
    public Posicao posicao() {
        return posicao;
    }

    @Override
    public char simbolo() {
        return '@';
    }

    public int capacidade() {
        return capacidade;
    }

    public int vidas() {
        return vidas;
    }

    public List<Passageiro> passageiros() {
        return List.copyOf(passageiros);
    }

    public boolean embarcar(Passageiro passageiro) {
        if (passageiros.size() == capacidade) {
            return false;
        }
        passageiros.add(passageiro);
        return true;
    }

    public int desembarcarTodos() {
        int quantidade = passageiros.size();
        passageiros.clear();
        return quantidade;
    }

    public boolean mover(int dx, int dy, int limite) {
        Posicao destino = posicao.deslocar(dx, dy);
        if (Math.abs(destino.x()) > limite || Math.abs(destino.y()) > limite) {
            return false;
        }
        posicao = destino;
        return true;
    }

    public void perderVida() {
        vidas = Math.max(0, vidas - 1);
    }
}
