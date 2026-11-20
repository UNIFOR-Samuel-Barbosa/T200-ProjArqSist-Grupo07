package br.edu.unifor.missaomarte.application;

import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.Passageiro;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.LongSupplier;

public final class GameSession {
    private final Missao missao;
    private final Random random;
    private final String piloto;
    private final String dificuldade;
    private EstadoPartida estado = EstadoPartida.EM_ANDAMENTO;
    private int pontuacao;
    private int movimentos;
    private final LongSupplier relogioMillis;
    private final long inicioMillis;
    private long fimMillis = -1;

    public GameSession(Missao missao, Random random, String piloto, String dificuldade, int pontuacaoInicial) {
        this(missao, random, piloto, dificuldade, pontuacaoInicial, System::currentTimeMillis);
    }

    /** O relógio é injetável para os testes não dependerem do tempo real. */
    public GameSession(Missao missao, Random random, String piloto, String dificuldade, int pontuacaoInicial,
                       LongSupplier relogioMillis) {
        this.missao = missao;
        this.random = random;
        this.piloto = piloto;
        this.dificuldade = dificuldade;
        this.pontuacao = pontuacaoInicial;
        this.relogioMillis = relogioMillis;
        this.inicioMillis = relogioMillis.getAsLong();
    }

    /** Tempo de partida; para de correr quando a partida termina. */
    public long duracaoSegundos() {
        long fim = fimMillis >= 0 ? fimMillis : relogioMillis.getAsLong();
        return (fim - inicioMillis) / 1000;
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
            fimMillis = relogioMillis.getAsLong();
            return new ResultadoTurno(false, "Missão abortada.", estado);
        }

        StringBuilder mensagem = new StringBuilder();
        List<EventoTurno> eventos = new ArrayList<>();
        if (acao.movimento()) {
            boolean moveu = missao.nave().mover(acao.dx(), acao.dy(), missao.limite());
            pontuacao--;
            movimentos++;
            if (moveu) {
                eventos.add(EventoTurno.MOVIMENTO);
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
                eventos.add(EventoTurno.EMBARQUE);
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
            eventos.add(EventoTurno.COLISAO);
            mensagem.append(" Colisão: perdeu uma vida e 5 pontos.");
        }

        int entreguesNesteTurno = missao.entregarNaBase();
        if (entreguesNesteTurno > 0) {
            eventos.add(EventoTurno.ENTREGA);
            mensagem.append(" Passageiros entregues: ").append(entreguesNesteTurno).append('.');
        }

        if (missao.nave().vidas() == 0 || pontuacao <= 0) {
            estado = EstadoPartida.DERROTA;
            eventos.add(EventoTurno.DERROTA);
            mensagem.append(" A missão foi perdida.");
        } else if (missao.passageirosEntregues() == missao.totalPassageiros()) {
            estado = EstadoPartida.VITORIA;
            eventos.add(EventoTurno.VITORIA);
            mensagem.append(" Missão concluída!");
        }
        if (estado != EstadoPartida.EM_ANDAMENTO) {
            fimMillis = relogioMillis.getAsLong();
        }
        return new ResultadoTurno(true, mensagem.toString(), estado, eventos);
    }
}
