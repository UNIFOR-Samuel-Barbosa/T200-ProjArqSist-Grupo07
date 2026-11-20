package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.Acao;
import br.edu.unifor.missaomarte.application.EstadoPartida;
import br.edu.unifor.missaomarte.application.EventoTurno;
import br.edu.unifor.missaomarte.application.GameSession;
import br.edu.unifor.missaomarte.application.ResultadoTurno;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.Posicao;
import br.edu.unifor.missaomarte.gdx.hud.HudRenderer;
import br.edu.unifor.missaomarte.gdx.recursos.Imagem;
import br.edu.unifor.missaomarte.gdx.recursos.Som;
import br.edu.unifor.missaomarte.gdx.visual.EfeitoUnico;
import br.edu.unifor.missaomarte.gdx.visual.MundoRenderer;
import br.edu.unifor.missaomarte.gdx.visual.PosicoesVisuais;
import br.edu.unifor.missaomarte.gdx.visual.TextoFlutuante;
import br.edu.unifor.missaomarte.gdx.visual.Tremor;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Tela da partida: encaminha cada ação à {@link GameSession} e reage aos eventos do turno com sons e
 * efeitos. Não decide regra nenhuma (colisão, pontos, vitória vêm da sessão); só apresenta.
 */
final class PartidaScreen extends TelaBase {
    private static final float DURACAO_EXPLOSAO = 0.6f;
    private static final float DURACAO_BRILHO = 0.5f;
    private static final float DURACAO_FLASH = 0.35f;
    private static final float DURACAO_PISCAR = 1.0f;

    private final GameSession sessao;
    private final Missao missao;
    private final int vidasIniciais;
    private final MapaGeometria geometria = new MapaGeometria(272, 296, 44, 5);
    private final PosicoesVisuais posicoes = new PosicoesVisuais();
    private final RepetidorDeTecla repetidor = new RepetidorDeTecla();
    private final List<EfeitoUnico> efeitos = new ArrayList<>();
    private final List<TextoFlutuante> textos = new ArrayList<>();
    private final Tremor tremor = new Tremor(9f, 0.45f);
    private final MundoRenderer mundo;
    private final HudRenderer hud;
    private String mensagem = "Resgate os passageiros e volte à base.";
    private float rotacaoNave;
    private float flash;
    private float piscar;
    private boolean pontuacaoRegistrada;

    PartidaScreen(MissaoMarteGame jogo, String piloto, Dificuldade dificuldade) {
        super(jogo);
        missao = jogo.fabrica().criar(dificuldade, jogo.random(), 5);
        sessao = new GameSession(missao, jogo.random(), piloto, dificuldade.toString(),
                dificuldade.pontuacaoInicial());
        vidasIniciais = missao.nave().vidas();
        mundo = new MundoRenderer(jogo.batch(), jogo.formas(), jogo.recursos(), geometria);
        hud = new HudRenderer(jogo.batch(), jogo.formas(), jogo.fonte(), jogo.recursos());
    }

    @Override
    protected InputProcessor criarEntrada() {
        return new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (sessao.estado() == EstadoPartida.VITORIA) {
                    if (keycode == Input.Keys.ENTER) {
                        jogo.abrirRanking("Missão concluída por " + sessao.piloto() + " com "
                                + sessao.pontuacao() + " pontos em " + HudRenderer.formatarTempo(sessao.duracaoSegundos()) + ".");
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
                if (acao == Acao.INVALIDA) {
                    return false;
                }
                jogar(acao);
                repetidor.pressionar(acao);
                return true;
            }

            @Override
            public boolean keyUp(int keycode) {
                repetidor.soltar(TecladoParaAcao.converter(keycode));
                return true;
            }
        };
    }

    private void jogar(Acao acao) {
        if (sessao.estado() != EstadoPartida.EM_ANDAMENTO) {
            return;
        }
        if (acao.movimento()) {
            rotacaoNave = switch (acao) {
                case DIREITA -> -90;
                case BAIXO -> 180;
                case ESQUERDA -> 90;
                default -> 0;
            };
        }
        int pontosAntes = sessao.pontuacao();
        ResultadoTurno resultado = sessao.processar(acao);
        mensagem = resultado.mensagem();
        reagir(resultado, sessao.pontuacao() - pontosAntes);
        if (sessao.estado() != EstadoPartida.EM_ANDAMENTO) {
            repetidor.parar();
        }
        if (sessao.estado() == EstadoPartida.VITORIA && !pontuacaoRegistrada) {
            jogo.ranking().registrar(sessao.piloto(), sessao.pontuacao(), sessao.dificuldade());
            pontuacaoRegistrada = true;
        }
    }

    /** Traduz eventos em sons e efeitos. A decisão de que algo aconteceu já veio da sessão. */
    private void reagir(ResultadoTurno resultado, int variacaoDePontos) {
        Posicao nave = missao.nave().posicao();
        Posicao base = missao.base().posicao();
        if (resultado.teve(EventoTurno.MOVIMENTO)) {
            jogo.audio().tocar(Som.MOVER);
        }
        if (resultado.teve(EventoTurno.EMBARQUE)) {
            efeitos.add(new EfeitoUnico(Imagem.BRILHO, nave.x(), nave.y(), DURACAO_BRILHO));
            jogo.audio().tocar(Som.EMBARCAR);
        }
        if (resultado.teve(EventoTurno.ENTREGA)) {
            efeitos.add(new EfeitoUnico(Imagem.BRILHO, base.x(), base.y(), DURACAO_BRILHO));
            textos.add(new TextoFlutuante("Entregue!", base.x(), base.y() - 0.4f));
            jogo.audio().tocar(Som.ENTREGAR);
        }
        if (resultado.teve(EventoTurno.COLISAO)) {
            efeitos.add(new EfeitoUnico(Imagem.EXPLOSAO, nave.x(), nave.y(), DURACAO_EXPLOSAO));
            tremor.disparar();
            flash = DURACAO_FLASH;
            piscar = DURACAO_PISCAR;
            jogo.audio().tocar(Som.COLISAO);
        }
        TextoFlutuante pontos = TextoFlutuante.variacaoDePontos(variacaoDePontos, nave.x(), nave.y());
        if (pontos != null) {
            textos.add(pontos);
        }
        if (resultado.teve(EventoTurno.VITORIA) || resultado.teve(EventoTurno.DERROTA)) {
            jogo.audio().pausarMusica();
            jogo.audio().tocar(resultado.teve(EventoTurno.VITORIA) ? Som.VITORIA : Som.DERROTA);
        }
    }

    @Override
    public void render(float delta) {
        Acao repetida = repetidor.avancar(delta);
        if (repetida != null) {
            jogar(repetida);
        }
        posicoes.atualizar(missao.objetosRenderizaveis(), delta);
        efeitos.removeIf(efeito -> !efeito.avancar(delta));
        textos.removeIf(texto -> !texto.avancar(delta));
        tremor.avancar(delta);
        flash = Math.max(0, flash - delta);
        piscar = Math.max(0, piscar - delta);

        iniciarDesenho(delta);
        // O tremor desloca só a câmera do mundo; o HUD é desenhado com a câmera parada.
        jogo.camera().translate(tremor.deslocamentoX(), tremor.deslocamentoY());
        jogo.camera().update();
        aplicarProjecao();
        boolean naveVisivel = piscar <= 0 || ((int) (piscar * 12)) % 2 == 0;
        mundo.desenhar(missao, posicoes, new MundoRenderer.EstadoDaNave(rotacaoNave, naveVisivel), tempo,
                efeitos, flash / DURACAO_FLASH * 0.45f);
        desenharTextosFlutuantes();
        jogo.camera().translate(-tremor.deslocamentoX(), -tremor.deslocamentoY());
        jogo.camera().update();
        aplicarProjecao();

        hud.desenhar(sessao, vidasIniciais, mensagem, jogo.audio().mudo());
    }

    private void aplicarProjecao() {
        jogo.batch().setProjectionMatrix(jogo.camera().combined);
        jogo.formas().setProjectionMatrix(jogo.camera().combined);
    }

    private void desenharTextosFlutuantes() {
        jogo.batch().begin();
        for (TextoFlutuante texto : textos) {
            Color cor = texto.texto().startsWith("-") ? Paleta.FLASH : Paleta.DESTAQUE;
            jogo.fonte().setColor(cor.r, cor.g, cor.b, texto.alfa());
            jogo.fonte().draw(jogo.batch(), texto.texto(),
                    geometria.centroCelulaX(texto.x()) - 14, geometria.centroCelulaY(texto.y()) + 30);
        }
        jogo.batch().end();
    }

    @Override
    public void hide() {
        repetidor.parar();
        super.hide();
    }
}
