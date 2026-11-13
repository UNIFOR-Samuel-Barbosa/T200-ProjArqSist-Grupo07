package br.edu.unifor.missaomarte.console;

import br.edu.unifor.missaomarte.application.Acao;
import br.edu.unifor.missaomarte.application.GameSession;
import br.edu.unifor.missaomarte.application.MissaoFactory;
import br.edu.unifor.missaomarte.application.ResultadoTurno;
import br.edu.unifor.missaomarte.application.EstadoPartida;
import br.edu.unifor.missaomarte.application.ranking.RankingEntry;
import br.edu.unifor.missaomarte.application.ranking.RankingService;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.domain.Missao;

import java.util.Optional;
import java.util.Random;
import java.util.Scanner;

public final class ConsoleGame {
    private final Scanner entrada;
    private final Random random;
    private final MissaoFactory fabrica;
    private final RankingService ranking;
    private final ConsoleRenderer renderer = new ConsoleRenderer();

    public ConsoleGame(Scanner entrada, Random random, MissaoFactory fabrica, RankingService ranking) {
        this.entrada = entrada;
        this.random = random;
        this.fabrica = fabrica;
        this.ranking = ranking;
    }

    public void iniciar() {
        boolean executando = true;
        while (executando) {
            System.out.println("\n=== MISSÃO MARTE ===");
            System.out.println("1. Nova missão  2. Ranking  3. Limpar ranking  4. Sair");
            String opcao = lerLinha("Escolha uma opção: ");
            if (opcao == null) {
                System.out.println("\nEntrada encerrada. Até a próxima missão.");
                break;
            }
            switch (opcao.trim()) {
                case "1" -> iniciarPartida();
                case "2" -> exibirRanking();
                case "3" -> {
                    ranking.limpar();
                    System.out.println("Ranking apagado.");
                }
                case "4" -> executando = false;
                default -> System.out.println("Opção inválida.");
            }
        }
        System.out.println("Até a próxima missão.");
    }

    private void iniciarPartida() {
        String nomeInformado = lerLinha("Nome do piloto: ");
        if (nomeInformado == null) {
            System.out.println("\nEntrada encerrada. Retornando ao menu.");
            return;
        }
        String piloto = nomeInformado.trim();
        if (piloto.isEmpty()) {
            piloto = "Piloto";
        }
        Dificuldade dificuldade = lerDificuldade();
        if (dificuldade == null) {
            System.out.println("\nEntrada encerrada. Missão não iniciada.");
            return;
        }
        Missao missao = fabrica.criar(dificuldade, random, 5);
        GameSession sessao = new GameSession(missao, random, piloto, dificuldade.toString(),
                dificuldade.pontuacaoInicial());

        while (sessao.estado() == EstadoPartida.EM_ANDAMENTO) {
            renderer.desenhar(missao);
            System.out.printf("Pontos %d | Vidas %d | A bordo %d/%d | Entregues %d/%d | No mapa %d | Movimentos %d%n",
                    sessao.pontuacao(), missao.nave().vidas(), missao.nave().passageiros().size(),
                    missao.nave().capacidade(), sessao.passageirosEntregues(), missao.totalPassageiros(),
                    missao.passageiros().size(), sessao.movimentos());
            String comando = lerLinha("Comando W/A/S/D, E embarcar, Q sair: ");
            if (comando == null) {
                sessao.processar(Acao.ABORTAR);
                System.out.println("\nEntrada encerrada. Missão abortada.");
                break;
            }
            ResultadoTurno resultado = sessao.processar(Acao.interpretar(comando));
            System.out.println(resultado.mensagem());
        }

        if (sessao.estado() == EstadoPartida.VITORIA) {
            boolean entrou = ranking.registrar(piloto, sessao.pontuacao(), dificuldade.toString());
            System.out.println(entrou ? "Vitória! Pontuação registrada." : "Vitória! Pontuação fora do Top 10.");
        } else if (sessao.estado() == EstadoPartida.DERROTA) {
            System.out.println("Fim de jogo.");
        } else if (sessao.estado() == EstadoPartida.ABORTADA) {
            System.out.println("Missão encerrada. Voltando ao menu principal.");
        }
    }

    private Dificuldade lerDificuldade() {
        while (true) {
            String texto = lerLinha("Dificuldade (facil/medio/dificil): ");
            if (texto == null) {
                return null;
            }

            Optional<Dificuldade> dificuldade = Dificuldade.tentarInterpretar(texto);
            if (dificuldade.isPresent()) {
                return dificuldade.get();
            }
            System.out.println("Dificuldade inválida. Digite facil, medio ou dificil.");
        }
    }

    private String lerLinha(String prompt) {
        System.out.print(prompt);
        if (!entrada.hasNextLine()) {
            return null;
        }
        return entrada.nextLine();
    }

    private void exibirRanking() {
        System.out.println("\n=== TOP 10 ===");
        int posicao = 1;
        for (RankingEntry entrada : ranking.listar()) {
            System.out.printf("%d. %s - %d pontos (%s)%n",
                    posicao++, entrada.piloto(), entrada.pontuacao(), entrada.dificuldade());
        }
        if (posicao == 1) {
            System.out.println("Ainda não há pontuações.");
        }
    }
}
