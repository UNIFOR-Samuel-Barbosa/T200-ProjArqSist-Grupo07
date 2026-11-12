package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.Acao;
import br.edu.unifor.missaomarte.application.EstadoPartida;
import br.edu.unifor.missaomarte.application.GameSession;
import br.edu.unifor.missaomarte.application.ResultadoTurno;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.ObjetoRenderizavel;
import br.edu.unifor.missaomarte.domain.Posicao;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

final class PartidaScreen extends TelaBase {
    private final GameSession sessao;
    private final Missao missao;
    private final MapaGeometria geometria = new MapaGeometria(400, 300, 34, 5);
    private String mensagem = "Resgate os passageiros e volte à base.";
    private boolean pontuacaoRegistrada;

    PartidaScreen(MissaoMarteGame jogo, String piloto, Dificuldade dificuldade) {
        super(jogo);
        missao = jogo.fabrica().criar(dificuldade, jogo.random(), 5);
        sessao = new GameSession(missao, jogo.random(), piloto, dificuldade.toString(),
                dificuldade.pontuacaoInicial());
    }

    @Override
    protected InputProcessor criarEntrada() {
        return new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (sessao.estado() == EstadoPartida.VITORIA) {
                    if (keycode == Input.Keys.ENTER) {
                        jogo.abrirRanking("Missão concluída por " + sessao.piloto()
                                + " com " + sessao.pontuacao() + " pontos.");
                    }
                    return true;
                }
                if (sessao.estado() == EstadoPartida.DERROTA) {
                    if (keycode == Input.Keys.ENTER) {
                        jogo.abrirMenu();
                    }
                    return true;
                }

                Acao acao = TecladoParaAcao.converter(keycode);
                if (acao == Acao.ABORTAR) {
                    jogo.abrirMenu();
                    return true;
                }
                if (acao != Acao.INVALIDA) {
                    ResultadoTurno resultado = sessao.processar(acao);
                    mensagem = resultado.mensagem();
                    if (sessao.estado() == EstadoPartida.VITORIA && !pontuacaoRegistrada) {
                        jogo.ranking().registrar(sessao.piloto(), sessao.pontuacao(), sessao.dificuldade());
                        pontuacaoRegistrada = true;
                    }
                    return true;
                }
                return false;
            }
        };
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.015f, 0.02f, 0.07f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        iniciarDesenho();
        desenharTabuleiro();
        desenharInformacoes();
    }

    private void desenharTabuleiro() {
        float lado = geometria.tamanhoCelula();
        jogo.formas().begin(ShapeRenderer.ShapeType.Line);
        jogo.formas().setColor(Color.DARK_GRAY);
        for (int coordenada = -geometria.raio(); coordenada <= geometria.raio(); coordenada++) {
            for (int y = -geometria.raio(); y <= geometria.raio(); y++) {
                float xCentro = geometria.centroCelulaX(coordenada);
                float yCentro = geometria.centroCelulaY(y);
                jogo.formas().rect(xCentro - lado / 2, yCentro - lado / 2, lado, lado);
            }
        }
        jogo.formas().end();

        jogo.formas().begin(ShapeRenderer.ShapeType.Filled);
        for (ObjetoRenderizavel objeto : missao.objetosRenderizaveis()) {
            Posicao p = objeto.posicao();
            jogo.formas().setColor(corDoObjeto(objeto));
            jogo.formas().rect(geometria.centroCelulaX(p.x()) - 13,
                    geometria.centroCelulaY(p.y()) - 13, 26, 26);
        }
        jogo.formas().end();

        jogo.batch().begin();
        for (ObjetoRenderizavel objeto : missao.objetosRenderizaveis()) {
            Posicao p = objeto.posicao();
            jogo.fonte().setColor(corDoObjeto(objeto));
            jogo.fonte().draw(jogo.batch(), String.valueOf(objeto.simbolo()),
                    geometria.centroCelulaX(p.x()) - 5, geometria.centroCelulaY(p.y()) + 6);
        }
        jogo.fonte().setColor(Color.WHITE);
        jogo.batch().end();
    }

    private Color corDoObjeto(ObjetoRenderizavel objeto) {
        return switch (objeto.simbolo()) {
            case '@' -> Color.CYAN;
            case 'L' -> Color.GREEN;
            case '#', 'X' -> Color.ORANGE;
            case 'P' -> Color.SKY;
            case 'E' -> Color.YELLOW;
            case 'T' -> Color.PINK;
            default -> Color.WHITE;
        };
    }

    private void desenharInformacoes() {
        jogo.batch().begin();
        jogo.fonte().setColor(Color.WHITE);
        jogo.fonte().draw(jogo.batch(), sessao.piloto() + " | " + sessao.dificuldade(), 30, 570);
        jogo.fonte().draw(jogo.batch(), "Vidas: " + missao.nave().vidas()
                + "   Pontos: " + sessao.pontuacao(), 30, 540);
        jogo.fonte().draw(jogo.batch(), "A bordo " + missao.nave().passageiros().size()
                + "/" + missao.nave().capacidade() + " | Entregues " + sessao.passageirosEntregues()
                + "/" + missao.totalPassageiros() + " | No mapa " + missao.passageiros().size(), 30, 510);
        jogo.fonte().draw(jogo.batch(), "Movimentos: " + sessao.movimentos(), 30, 480);
        jogo.fonte().draw(jogo.batch(), mensagem, 30, 70);
        if (sessao.estado() == EstadoPartida.VITORIA) {
            jogo.fonte().draw(jogo.batch(), "VITORIA! Enter abre o ranking.", 260, 40);
        } else if (sessao.estado() == EstadoPartida.DERROTA) {
            jogo.fonte().draw(jogo.batch(), "FIM DE JOGO. Enter volta ao menu.", 260, 40);
        }
        jogo.batch().end();
    }
}
