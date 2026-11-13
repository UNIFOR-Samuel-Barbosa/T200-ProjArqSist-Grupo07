package br.edu.unifor.missaomarte.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;

abstract class TelaBase extends ScreenAdapter {
    protected final MissaoMarteGame jogo;

    TelaBase(MissaoMarteGame jogo) {
        this.jogo = jogo;
    }

    @Override
    public final void show() {
        registrar("show");
        Gdx.input.setInputProcessor(criarEntrada());
    }

    protected abstract InputProcessor criarEntrada();

    @Override
    public void resize(int width, int height) {
        registrar("resize");
        jogo.viewport().update(width, height, true);
    }

    @Override
    public void pause() {
        registrar("pause");
    }

    @Override
    public void resume() {
        registrar("resume");
    }

    @Override
    public void hide() {
        registrar("hide");
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        registrar("dispose");
    }

    protected void iniciarDesenho() {
        jogo.viewport().apply();
        jogo.batch().setProjectionMatrix(jogo.viewport().getCamera().combined);
        jogo.formas().setProjectionMatrix(jogo.viewport().getCamera().combined);
    }

    protected void registrar(String evento) {
        Gdx.app.log(getClass().getSimpleName(), evento + "()");
    }
}
