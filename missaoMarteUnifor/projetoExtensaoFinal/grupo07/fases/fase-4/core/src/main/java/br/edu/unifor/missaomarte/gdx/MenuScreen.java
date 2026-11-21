package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.gdx.recursos.Imagem;
import br.edu.unifor.missaomarte.gdx.recursos.Som;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;

final class MenuScreen extends TelaBase {
    private String mensagem = "";

    MenuScreen(MissaoMarteGame jogo) {
        super(jogo);
        botoes.add(new Botao("1  Nova partida", 280, 318, 240, 50, this::novaPartida));
        botoes.add(new Botao("2  Ranking", 280, 262, 240, 50, this::ranking));
        botoes.add(new Botao("3  Limpar ranking", 280, 206, 240, 50, this::limparRanking));
        botoes.add(new Botao("4  Sair", 280, 150, 240, 50, Gdx.app::exit));
    }

    @Override
    protected void aoMostrar() {
        // Volta da vitória/derrota: a música, pausada no fim da partida, é retomada.
        jogo.audio().iniciarMusica();
    }

    private void novaPartida() {
        jogo.audio().tocar(Som.SELECIONAR);
        jogo.abrirNovaPartida();
    }

    private void ranking() {
        jogo.audio().tocar(Som.SELECIONAR);
        jogo.abrirRanking("");
    }

    private void limparRanking() {
        jogo.audio().tocar(Som.SELECIONAR);
        jogo.ranking().limpar();
        mensagem = "Ranking limpo.";
    }

    @Override
    protected InputProcessor criarEntrada() {
        return new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.NUM_1 -> novaPartida();
                    case Input.Keys.NUM_2 -> ranking();
                    case Input.Keys.NUM_3 -> limparRanking();
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
        iniciarDesenho(delta);
        painel(240, 90, 320, 440);
        jogo.batch().begin();
        float flutuar = (float) Math.sin(tempo * 2.5f) * 6;
        jogo.batch().draw(jogo.recursos().emLaco(Imagem.PROPULSOR, tempo), 380, 400 + flutuar, 40, 40);
        jogo.batch().draw(jogo.recursos().regiao(Imagem.NAVE), 368, 420 + flutuar, 64, 64);
        texto("MISSÃO MARTE", 347, 405, Paleta.DESTAQUE);
        texto(mensagem, 350, 135, Paleta.DESTAQUE);
        texto(jogo.audio().mudo() ? "M: ligar som" : "M: desligar som", 345, 118, Paleta.TEXTO);
        jogo.batch().end();
        desenharBotoes();
    }
}
