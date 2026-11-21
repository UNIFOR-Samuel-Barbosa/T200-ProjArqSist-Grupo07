package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.MissaoFactory;
import br.edu.unifor.missaomarte.application.ranking.RankingService;
import br.edu.unifor.missaomarte.gdx.audio.Audio;
import br.edu.unifor.missaomarte.gdx.recursos.Recursos;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.Random;

public final class MissaoMarteGame extends Game {
    static final float LARGURA = 800;
    static final float ALTURA = 600;

    private final MissaoFactory fabrica;
    private final RankingService ranking;
    private final Random random;
    private final boolean audioAtivo;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private SpriteBatch batch;
    private ShapeRenderer formas;
    private BitmapFont fonte;
    private Recursos recursos;
    private Audio audio;

    public MissaoMarteGame(MissaoFactory fabrica, RankingService ranking, Random random, boolean audioAtivo) {
        this.fabrica = fabrica;
        this.ranking = ranking;
        this.random = random;
        this.audioAtivo = audioAtivo;
    }

    @Override
    public void create() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(LARGURA, ALTURA, camera);
        viewport.apply(true);
        batch = new SpriteBatch();
        formas = new ShapeRenderer();
        fonte = new BitmapFont();
        fonte.getData().markupEnabled = false;
        recursos = new Recursos(audioAtivo);
        audio = new Audio(recursos);
        audio.iniciarMusica();
        setScreen(new MenuScreen(this));
    }

    /** O {@code Game} só chama {@code hide()} na tela antiga; aqui ela também é descartada. */
    @Override
    public void setScreen(com.badlogic.gdx.Screen nova) {
        com.badlogic.gdx.Screen antiga = getScreen();
        super.setScreen(nova);
        if (antiga != null) {
            antiga.dispose();
        }
    }

    public void abrirNovaPartida() {
        setScreen(new NovaPartidaScreen(this));
    }

    public void abrirMenu() {
        setScreen(new MenuScreen(this));
    }

    public void abrirRanking(String resumo) {
        setScreen(new RankingScreen(this, resumo));
    }

    MissaoFactory fabrica() {
        return fabrica;
    }

    RankingService ranking() {
        return ranking;
    }

    Random random() {
        return random;
    }

    FitViewport viewport() {
        return viewport;
    }

    OrthographicCamera camera() {
        return camera;
    }

    SpriteBatch batch() {
        return batch;
    }

    ShapeRenderer formas() {
        return formas;
    }

    BitmapFont fonte() {
        return fonte;
    }

    Recursos recursos() {
        return recursos;
    }

    Audio audio() {
        return audio;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (getScreen() != null) {
            getScreen().dispose();
        }
        batch.dispose();
        formas.dispose();
        fonte.dispose();
        recursos.dispose();
    }
}
