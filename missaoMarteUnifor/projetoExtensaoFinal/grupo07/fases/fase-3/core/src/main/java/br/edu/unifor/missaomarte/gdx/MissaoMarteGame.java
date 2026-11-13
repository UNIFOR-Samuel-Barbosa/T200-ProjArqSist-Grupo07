package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.MissaoFactory;
import br.edu.unifor.missaomarte.application.ranking.RankingService;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.Random;

public final class MissaoMarteGame extends Game {
    private final MissaoFactory fabrica;
    private final RankingService ranking;
    private final Random random;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private SpriteBatch batch;
    private ShapeRenderer formas;
    private BitmapFont fonte;

    public MissaoMarteGame(MissaoFactory fabrica, RankingService ranking, Random random) {
        this.fabrica = fabrica;
        this.ranking = ranking;
        this.random = random;
    }

    @Override
    public void create() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(800, 600, camera);
        viewport.apply(true);
        camera.position.set(400, 300, 0);
        camera.update();
        batch = new SpriteBatch();
        formas = new ShapeRenderer();
        fonte = new BitmapFont();
        setScreen(new MenuScreen(this));
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

    SpriteBatch batch() {
        return batch;
    }

    ShapeRenderer formas() {
        return formas;
    }

    BitmapFont fonte() {
        return fonte;
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        formas.dispose();
        fonte.dispose();
    }
}
