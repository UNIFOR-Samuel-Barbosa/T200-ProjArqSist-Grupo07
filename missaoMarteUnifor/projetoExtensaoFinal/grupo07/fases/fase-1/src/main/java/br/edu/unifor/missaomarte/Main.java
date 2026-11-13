package br.edu.unifor.missaomarte;

import br.edu.unifor.missaomarte.application.MissaoFactory;
import br.edu.unifor.missaomarte.application.ranking.RankingService;
import br.edu.unifor.missaomarte.console.ConsoleGame;
import br.edu.unifor.missaomarte.infrastructure.ranking.FileRankingRepository;

import java.nio.file.Path;
import java.util.Random;
import java.util.Scanner;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        RankingService ranking = new RankingService(
                new FileRankingRepository(Path.of("ranking.json")));
        ConsoleGame jogo = new ConsoleGame(new Scanner(System.in), new Random(),
                new MissaoFactory(), ranking);
        jogo.iniciar();
    }
}
