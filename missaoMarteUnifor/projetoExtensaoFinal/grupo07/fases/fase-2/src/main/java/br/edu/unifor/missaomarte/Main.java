package br.edu.unifor.missaomarte;

import br.edu.unifor.missaomarte.application.MissaoFactory;
import br.edu.unifor.missaomarte.application.ranking.RankingService;
import br.edu.unifor.missaomarte.console.ConsoleGame;
import br.edu.unifor.missaomarte.infrastructure.ranking.FileRankingRepository;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Random;
import java.util.Scanner;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        configurarCodificacaoUtf8();
        RankingService ranking = new RankingService(
                new FileRankingRepository(Path.of("ranking.json")));
        ConsoleGame jogo = new ConsoleGame(new Scanner(System.in, StandardCharsets.UTF_8), new Random(),
                new MissaoFactory(), ranking);
        jogo.iniciar();
    }

    private static void configurarCodificacaoUtf8() {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err), true, StandardCharsets.UTF_8));
    }
}
