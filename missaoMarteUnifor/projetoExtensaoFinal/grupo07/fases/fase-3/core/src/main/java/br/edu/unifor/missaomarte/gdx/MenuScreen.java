package br.edu.unifor.missaomarte.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;

final class MenuScreen extends TelaBase {
    private String mensagem = "";

    MenuScreen(MissaoMarteGame jogo) {
        super(jogo);
    }

    @Override
    protected InputProcessor criarEntrada() {
        return new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.NUM_1 -> jogo.abrirNovaPartida();
                    case Input.Keys.NUM_2 -> jogo.abrirRanking("");
                    case Input.Keys.NUM_3 -> {
                        jogo.ranking().limpar();
                        mensagem = "Ranking limpo.";
                    }
                    case Input.Keys.NUM_4, Input.Keys.ESCAPE -> Gdx.app.exit();
                    default -> {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.02f, 0.03f, 0.10f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        iniciarDesenho();
        jogo.batch().begin();
        jogo.fonte().draw(jogo.batch(), "MISSAO MARTE", 300, 480);
        jogo.fonte().draw(jogo.batch(), "1 - Nova partida", 300, 420);
        jogo.fonte().draw(jogo.batch(), "2 - Ranking", 300, 385);
        jogo.fonte().draw(jogo.batch(), "3 - Limpar ranking", 300, 350);
        jogo.fonte().draw(jogo.batch(), "4 - Sair", 300, 315);
        jogo.fonte().draw(jogo.batch(), mensagem, 300, 260);
        jogo.batch().end();
    }
}
