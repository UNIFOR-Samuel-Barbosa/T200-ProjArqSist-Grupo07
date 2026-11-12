package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.domain.Dificuldade;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;

final class NovaPartidaScreen extends TelaBase {
    private final StringBuilder nome = new StringBuilder();
    private Dificuldade dificuldade = Dificuldade.MEDIO;

    NovaPartidaScreen(MissaoMarteGame jogo) {
        super(jogo);
    }

    @Override
    protected InputProcessor criarEntrada() {
        return new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.F1 -> dificuldade = Dificuldade.FACIL;
                    case Input.Keys.F2 -> dificuldade = Dificuldade.MEDIO;
                    case Input.Keys.F3 -> dificuldade = Dificuldade.DIFICIL;
                    case Input.Keys.BACKSPACE -> {
                        if (!nome.isEmpty()) {
                            nome.deleteCharAt(nome.length() - 1);
                        }
                    }
                    case Input.Keys.ENTER -> jogo.setScreen(new PartidaScreen(jogo,
                            nome.isEmpty() ? "Piloto" : nome.toString(), dificuldade));
                    case Input.Keys.ESCAPE -> jogo.abrirMenu();
                    default -> {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public boolean keyTyped(char character) {
                if (!Character.isISOControl(character) && nome.length() < 24) {
                    nome.append(character);
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
        jogo.fonte().draw(jogo.batch(), "NOVA MISSAO", 300, 480);
        jogo.fonte().draw(jogo.batch(), "Piloto: " + nome + "_", 260, 420);
        jogo.fonte().draw(jogo.batch(), "F1 Facil   F2 Medio   F3 Dificil", 260, 375);
        jogo.fonte().draw(jogo.batch(), "Escolhida: " + dificuldade, 260, 340);
        jogo.fonte().draw(jogo.batch(), "Enter inicia | Backspace apaga | Esc volta", 210, 280);
        jogo.batch().end();
    }
}
