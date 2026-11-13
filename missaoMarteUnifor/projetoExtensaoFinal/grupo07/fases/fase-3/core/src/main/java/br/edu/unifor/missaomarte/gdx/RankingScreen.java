package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.ranking.RankingEntry;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.Gdx;

import java.util.List;

final class RankingScreen extends TelaBase {
    private final String resumo;
    private final List<RankingEntry> entradas;

    RankingScreen(MissaoMarteGame jogo, String resumo) {
        super(jogo);
        this.resumo = resumo;
        entradas = jogo.ranking().listar();
    }

    @Override
    protected InputProcessor criarEntrada() {
        return new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ENTER || keycode == Input.Keys.ESCAPE) {
                    jogo.abrirMenu();
                    return true;
                }
                return false;
            }
        };
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.02f, 0.03f, 0.10f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        iniciarDesenho();
        jogo.batch().begin();
        jogo.fonte().setColor(Color.WHITE);
        jogo.fonte().draw(jogo.batch(), "RANKING TOP 10", 310, 540);
        if (!resumo.isBlank()) {
            jogo.fonte().draw(jogo.batch(), resumo, 120, 500);
        }
        if (entradas.isEmpty()) {
            jogo.fonte().draw(jogo.batch(), "Sem pontuacoes registradas.", 270, 450);
        }
        for (int i = 0; i < entradas.size(); i++) {
            RankingEntry entrada = entradas.get(i);
            jogo.fonte().draw(jogo.batch(), (i + 1) + ". " + entrada.piloto()
                    + " - " + entrada.pontuacao() + " (" + entrada.dificuldade() + ")",
                    220, 450 - i * 30);
        }
        jogo.fonte().draw(jogo.batch(), "Enter/Esc volta ao menu", 300, 80);
        jogo.batch().end();
    }
}
