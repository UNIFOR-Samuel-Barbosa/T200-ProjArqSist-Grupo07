package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.ranking.RankingEntry;
import br.edu.unifor.missaomarte.gdx.recursos.Som;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;

import java.util.List;

final class RankingScreen extends TelaBase {
    private final String resumo;
    private final List<RankingEntry> entradas;

    RankingScreen(MissaoMarteGame jogo, String resumo) {
        super(jogo);
        this.resumo = resumo;
        entradas = jogo.ranking().listar();
        botoes.add(new Botao("Enter  Voltar ao menu", 290, 70, 220, 48, this::voltar));
    }

    private void voltar() {
        jogo.audio().tocar(Som.SELECIONAR);
        jogo.abrirMenu();
    }

    @Override
    protected InputProcessor criarEntrada() {
        return new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ENTER || keycode == Input.Keys.ESCAPE) {
                    voltar();
                    return true;
                }
                return false;
            }
        };
    }

    @Override
    public void render(float delta) {
        iniciarDesenho(delta);
        painel(110, 40, 580, 520);
        jogo.batch().begin();
        texto("RANKING TOP 10", 335, 535, Paleta.DESTAQUE);
        if (!resumo.isBlank()) {
            texto(resumo, 140, 500, Paleta.TEXTO);
        }
        if (entradas.isEmpty()) {
            texto("Sem pontuações registradas.", 300, 440, Paleta.TEXTO);
        }
        for (int i = 0; i < entradas.size(); i++) {
            RankingEntry entrada = entradas.get(i);
            texto((i + 1) + ". " + entrada.piloto() + " - " + entrada.pontuacao() + " (" + entrada.dificuldade() + ")",
                    200, 450 - i * 32, i == 0 ? Paleta.DESTAQUE : Paleta.TEXTO);
        }
        jogo.batch().end();
        desenharBotoes();
    }
}
