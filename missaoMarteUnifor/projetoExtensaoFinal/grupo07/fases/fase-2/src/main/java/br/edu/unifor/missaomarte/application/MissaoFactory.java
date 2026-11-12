package br.edu.unifor.missaomarte.application;

import br.edu.unifor.missaomarte.domain.Asteroide;
import br.edu.unifor.missaomarte.domain.Astronauta;
import br.edu.unifor.missaomarte.domain.Base;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.domain.Engenheiro;
import br.edu.unifor.missaomarte.domain.Inimigo;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.Nave;
import br.edu.unifor.missaomarte.domain.Passageiro;
import br.edu.unifor.missaomarte.domain.Posicao;
import br.edu.unifor.missaomarte.domain.Professor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public final class MissaoFactory {
    private static final String[] NOMES = {"Lia", "Caio", "Ravi", "Nina", "Bento"};

    public Missao criar(Dificuldade dificuldade, Random random, int limite) {
        if (limite < 2) {
            throw new IllegalArgumentException("O mapa precisa ter raio de pelo menos 2.");
        }

        Posicao base = new Posicao(0, 0);
        Posicao inicio = new Posicao(0, 1);
        Set<Posicao> ocupadas = new HashSet<>(List.of(base, inicio));
        List<Passageiro> passageiros = new ArrayList<>();
        List<Asteroide> asteroides = new ArrayList<>();
        List<Inimigo> inimigos = new ArrayList<>();

        for (int i = 0; i < dificuldade.quantidadePassageiros(); i++) {
            Posicao posicao = posicaoLivre(random, limite, ocupadas);
            Passageiro passageiro = switch (i % 3) {
                case 0 -> new Professor(NOMES[i % NOMES.length], posicao);
                case 1 -> new Engenheiro(NOMES[i % NOMES.length], posicao);
                default -> new Astronauta(NOMES[i % NOMES.length], posicao);
            };
            passageiros.add(passageiro);
        }
        for (int i = 0; i < dificuldade.quantidadeAsteroides(); i++) {
            asteroides.add(new Asteroide(posicaoLivre(random, limite, ocupadas)));
        }
        for (int i = 0; i < dificuldade.quantidadeInimigos(); i++) {
            inimigos.add(new Inimigo(posicaoLivre(random, limite, ocupadas)));
        }

        return new Missao(new Nave(inicio, dificuldade.quantidadePassageiros()),
                new Base(base), limite, passageiros, asteroides, inimigos);
    }

    private Posicao posicaoLivre(Random random, int limite, Set<Posicao> ocupadas) {
        while (true) {
            Posicao candidata = new Posicao(random.nextInt(limite * 2 + 1) - limite,
                    random.nextInt(limite * 2 + 1) - limite);
            if (ocupadas.add(candidata)) {
                return candidata;
            }
        }
    }
}
