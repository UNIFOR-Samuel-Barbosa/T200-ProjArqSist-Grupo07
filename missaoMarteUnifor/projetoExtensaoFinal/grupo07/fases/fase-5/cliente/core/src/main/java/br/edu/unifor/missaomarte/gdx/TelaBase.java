package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.gdx.hud.HudRenderer;
import br.edu.unifor.missaomarte.gdx.recursos.Imagem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

/**
 * Ciclo de vida comum das telas (Template Method): instala a entrada em {@code show()}, remove em
 * {@code hide()} e oferece o desenho do fundo e de painéis. O atalho {@code M} (som) é instalado aqui,
 * uma única vez para todas as telas; telas com digitação livre desligam o atalho. Os botões de cada tela
 * também são desenhados e atendidos (clique) aqui; a tela só declara rótulo, posição e ação.
 */
abstract class TelaBase extends ScreenAdapter {
    protected final MissaoMarteGame jogo;
    protected float tempo;
    protected final List<Botao> botoes = new ArrayList<>();
    private final GlyphLayout layout = new GlyphLayout();
    private final Vector2 ponto = new Vector2();
    private Botao sobOPonteiro;

    TelaBase(MissaoMarteGame jogo) {
        this.jogo = jogo;
    }

    @Override
    public final void show() {
        registrar("show");
        InputMultiplexer entrada = new InputMultiplexer();
        if (aceitaAtalhoDeSom()) {
            entrada.addProcessor(new InputAdapter() {
                @Override
                public boolean keyDown(int keycode) {
                    if (keycode == Input.Keys.M) {
                        jogo.audio().alternarMudo();
                        return true;
                    }
                    return false;
                }
            });
        }
        entrada.addProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int telaX, int telaY, int ponteiro, int botao) {
                Botao alvo = botaoEm(telaX, telaY);
                if (alvo == null) {
                    return false;
                }
                alvo.acao().run();
                return true;
            }

            @Override
            public boolean mouseMoved(int telaX, int telaY) {
                sobOPonteiro = botaoEm(telaX, telaY);
                return false;
            }
        });
        entrada.addProcessor(criarEntrada());
        Gdx.input.setInputProcessor(entrada);
        aoMostrar();
    }

    protected abstract InputProcessor criarEntrada();

    /** Telas em que a letra M faz parte de um texto digitado devolvem {@code false}. */
    protected boolean aceitaAtalhoDeSom() {
        return true;
    }

    protected void aoMostrar() {
    }

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

    /** Limpa a tela, aplica a projeção e desenha a imagem de fundo. */
    protected void iniciarDesenho(float delta) {
        tempo += delta;
        Gdx.gl.glClearColor(0.10f, 0.07f, 0.18f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        jogo.viewport().apply();
        jogo.batch().setProjectionMatrix(jogo.camera().combined);
        jogo.formas().setProjectionMatrix(jogo.camera().combined);
        jogo.batch().begin();
        jogo.batch().draw(jogo.recursos().regiao(Imagem.FUNDO), 0, 0, MissaoMarteGame.LARGURA, MissaoMarteGame.ALTURA);
        jogo.batch().end();
    }

    protected void painel(float x, float y, float largura, float altura) {
        HudRenderer.painel(jogo.batch(), jogo.recursos(), x, y, largura, altura);
    }

    /** Converte a posição do ponteiro (pixels da janela) para o mundo 800x600 e procura o botão. */
    private Botao botaoEm(int telaX, int telaY) {
        jogo.viewport().unproject(ponto.set(telaX, telaY));
        return botoes.stream().filter(b -> b.contem(ponto.x, ponto.y)).findFirst().orElse(null);
    }

    /** Botão em menta: o que está sob o ponteiro; telas podem destacar também a opção escolhida. */
    protected boolean destacado(Botao botao) {
        return botao == sobOPonteiro;
    }

    protected void desenharBotoes() {
        jogo.batch().begin();
        jogo.batch().setColor(1, 1, 1, 1);
        for (Botao botao : botoes) {
            Imagem imagem = destacado(botao) ? Imagem.BOTAO_DESTAQUE : Imagem.BOTAO;
            jogo.batch().draw(jogo.recursos().regiao(imagem), botao.x(), botao.y(), botao.largura(), botao.altura());
            jogo.fonte().setColor(Paleta.TEXTO);
            layout.setText(jogo.fonte(), botao.rotulo());
            jogo.fonte().draw(jogo.batch(), layout, botao.x() + (botao.largura() - layout.width) / 2,
                    botao.y() + (botao.altura() + layout.height) / 2 + 1);
        }
        jogo.batch().end();
    }

    /** Escreve um texto; deve ser chamado entre batch.begin() e batch.end(). */
    protected void texto(String texto, float x, float y, Color cor) {
        jogo.fonte().setColor(cor);
        jogo.fonte().draw(jogo.batch(), texto, x, y);
    }

    protected void registrar(String evento) {
        Gdx.app.log(getClass().getSimpleName(), evento + "()");
    }
}
