package br.edu.unifor.missaomarte.lwjgl3;

import br.edu.unifor.missaomarte.application.MissaoFactory;
import br.edu.unifor.missaomarte.application.ranking.RankingService;
import br.edu.unifor.missaomarte.gdx.MissaoMarteGame;
import br.edu.unifor.missaomarte.infrastructure.ranking.FileRankingRepository;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import java.nio.file.Path;
import java.util.Random;

public final class Lwjgl3Launcher {
    private Lwjgl3Launcher() {
    }

    public static void main(String[] args) {
        RankingService ranking = new RankingService(
                new FileRankingRepository(Path.of("ranking.json")));
        MissaoMarteGame jogo = new MissaoMarteGame(new MissaoFactory(), ranking, new Random());
        Lwjgl3ApplicationConfiguration configuracao = new Lwjgl3ApplicationConfiguration();
        configuracao.setTitle("Missão Marte — Grupo 07");
        configuracao.setWindowedMode(800, 600);
        configuracao.useVsync(true);
        configuracao.setForegroundFPS(60);
        new Lwjgl3Application(jogo, configuracao);
    }
}
