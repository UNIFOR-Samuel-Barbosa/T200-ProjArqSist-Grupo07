package br.edu.unifor.missaomarte.application;

import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.Passageiro;

import java.util.Random;

public final class GameSession {
    private final Missao missao;
    private final Random random;
    private final String piloto;
    private final String dificuldade;
    private EstadoPartida estado = EstadoPartida.EM_ANDAMENTO;
    private int pontuacao;
    private int movimentos;

    public GameSession(Missao missao, Random random, String piloto, String dificuldade, int pontuacaoInicial) {
        this.missao = missao;
        this.random = random;
        this.piloto = piloto;
        this.dificuldade = dificuldade;
        this.pontuacao = pontuacaoInicial;
    }

    public Missao missao() {
        return missao;
    }

    public String piloto() {
        return piloto;
    }

    public String dificuldade() {
        return dificuldade;
    }

    public EstadoPartida estado() {
        return estado;
    }

    public int pontuacao() {
        return pontuacao;
    }

    public int movimentos() {
        return movimentos;
    }

    public int passageirosEntregues() {
        return missao.passageirosEntregues();
    }

    public ResultadoTurno processar(Acao acao) {
        if (estado != EstadoPartida.EM_ANDAMENTO) {
            return new ResultadoTurno(false, "A partida já terminou.", estado);
        }
        if (acao == Acao.INVALIDA) {
            return new ResultadoTurno(false, "Comando inválido.", estado);
        }
        if (acao == Acao.ABORTAR) {
            estado = EstadoPartida.ABORTADA;
            return new ResultadoTurno(false, "Missão abortada.", estado);
        }

        StringBuilder mensagem = new StringBuilder();
        if (acao.movimento()) {
            boolean moveu = missao.nave().mover(acao.dx(), acao.dy(), missao.limite());
            pontuacao--;
            movimentos++;
            if (moveu) {
                mensagem.append("Nave movida para ").append(missao.nave().posicao()).append('.');
            } else {
                mensagem.append("Limite do mapa atingido; nave permanece em ")
                        .append(missao.nave().posicao()).append('.');
            }
        } else {
            Passageiro passageiro = missao.passageiros().stream()
                    .filter(p -> p.posicao().equals(missao.nave().posicao()))
                    .findFirst().orElse(null);
            if (passageiro == null) {
                mensagem.append("Nenhum passageiro na posição ").append(missao.nave().posicao()).append('.');
            } else if (missao.embarcarNaPosicao()) {
                pontuacao += passageiro.valorResgate();
                mensagem.append("Passageiro ").append(passageiro.nome()).append(" embarcado em ")
                        .append(missao.nave().posicao()).append('.');
            } else {
                mensagem.append("A nave está sem vagas.");
            }
        }

        missao.moverInimigos(random);
        if (missao.colidiu()) {
            missao.nave().perderVida();
            pontuacao -= 5;
            mensagem.append(" Colisão: perdeu uma vida e 5 pontos.");
        }

        int entreguesNesteTurno = missao.entregarNaBase();
        if (entreguesNesteTurno > 0) {
            mensagem.append(" Passageiros entregues: ").append(entreguesNesteTurno).append('.');
        }

        if (missao.nave().vidas() == 0 || pontuacao <= 0) {
            estado = EstadoPartida.DERROTA;
            mensagem.append(" A missão foi perdida.");
        } else if (missao.passageirosEntregues() == missao.totalPassageiros()) {
            estado = EstadoPartida.VITORIA;
            mensagem.append(" Missão concluída!");
        }
        return new ResultadoTurno(true, mensagem.toString(), estado);
    }
}
