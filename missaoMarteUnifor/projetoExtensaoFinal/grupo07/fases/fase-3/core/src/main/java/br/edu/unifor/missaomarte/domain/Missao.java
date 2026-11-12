package br.edu.unifor.missaomarte.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Missao {
    private final Nave nave;
    private final Base base;
    private final int limite;
    private final List<Passageiro> passageiros;
    private final List<Asteroide> asteroides;
    private final List<Inimigo> inimigos;
    private final int totalPassageiros;

    public Missao(Nave nave, Base base, int limite, List<Passageiro> passageiros,
                  List<Asteroide> asteroides, List<Inimigo> inimigos) {
        this.nave = nave;
        this.base = base;
        this.limite = limite;
        this.passageiros = new ArrayList<>(passageiros);
        this.asteroides = new ArrayList<>(asteroides);
        this.inimigos = new ArrayList<>(inimigos);
        this.totalPassageiros = passageiros.size();
    }

    public Nave nave() {
        return nave;
    }

    public Base base() {
        return base;
    }

    public int limite() {
        return limite;
    }

    public List<Passageiro> passageiros() {
        return List.copyOf(passageiros);
    }

    public List<Asteroide> asteroides() {
        return List.copyOf(asteroides);
    }

    public List<Inimigo> inimigos() {
        return List.copyOf(inimigos);
    }

    public int totalPassageiros() {
        return totalPassageiros;
    }

    public int passageirosEntregues() {
        return totalPassageiros - passageiros.size() - nave.passageiros().size();
    }

    public List<ObjetoRenderizavel> objetosRenderizaveis() {
        List<ObjetoRenderizavel> objetos = new ArrayList<>();
        objetos.add(base);
        objetos.addAll(passageiros);
        objetos.addAll(asteroides);
        objetos.addAll(inimigos);
        objetos.add(nave);
        return List.copyOf(objetos);
    }

    public boolean embarcarNaPosicao() {
        for (int indice = 0; indice < passageiros.size(); indice++) {
            Passageiro passageiro = passageiros.get(indice);
            if (passageiro.posicao().equals(nave.posicao()) && nave.embarcar(passageiro)) {
                passageiros.remove(indice);
                return true;
            }
        }
        return false;
    }

    public boolean passageiroNaPosicao() {
        return passageiros.stream().anyMatch(p -> p.posicao().equals(nave.posicao()));
    }

    public int entregarNaBase() {
        if (!nave.posicao().equals(base.posicao())) {
            return 0;
        }
        return nave.desembarcarTodos();
    }

    public boolean colidiu() {
        return asteroides.stream().anyMatch(a -> a.posicao().equals(nave.posicao()))
                || inimigos.stream().anyMatch(i -> i.posicao().equals(nave.posicao()));
    }

    public void moverInimigos(Random random) {
        inimigos.forEach(inimigo -> inimigo.mover(random, limite));
    }
}
