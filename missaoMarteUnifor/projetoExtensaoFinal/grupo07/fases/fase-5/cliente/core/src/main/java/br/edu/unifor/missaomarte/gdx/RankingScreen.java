package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.servidor.LinhaDoRanking;
import br.edu.unifor.missaomarte.gdx.recursos.Som;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Convidado: ranking local, lido na hora (como na Fase 4). Logado: ranking global, pedido ao servidor
 * sem travar o desenho, com estados visíveis (carregando, pronto, erro) e {@code R} para tentar de novo.
 */
final class RankingScreen extends TelaBase {
    private enum Estado { CARREGANDO, PRONTO, ERRO }

    private final String resumo;
    private final boolean global;
    private List<String> linhas = new ArrayList<>();
    private Estado estado = Estado.PRONTO;
    private String erro = "";
    private int linhaDoJogador = -1;

    RankingScreen(MissaoMarteGame jogo, String resumo) {
        super(jogo);
        this.resumo = resumo;
        this.global = jogo.logado();
        botoes.add(new Botao("Enter  Voltar ao menu", 290, 58, 220, 46, this::voltar));
        if (global) {
            carregar();
        } else {
            jogo.ranking().listar().forEach(e -> linhas.add((linhas.size() + 1) + ". " + e.piloto() + "  -  "
                    + e.pontuacao() + " pts  (" + e.dificuldade() + ")"));
        }
    }

    private void carregar() {
        estado = Estado.CARREGANDO;
        jogo.servidor().carregarRanking(jogo.sessao(), resposta -> {
            if (jogo.getScreen() != this) {
                return;
            }
            if (resposta.sucesso()) {
                linhas = new ArrayList<>();
                linhaDoJogador = -1;
                for (LinhaDoRanking linha : resposta.valor()) {
                    if (linha.nome().equalsIgnoreCase(jogo.sessao().nome())) {
                        linhaDoJogador = linhas.size();
                    }
                    linhas.add(Formatos.linha(linha));
                }
                estado = Estado.PRONTO;
            } else if (resposta.naoAutorizado()) {
                jogo.sessaoExpirada(resposta.erro());
            } else {
                erro = resposta.erro();
                estado = Estado.ERRO;
            }
        });
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
                if (keycode == Input.Keys.R && global && estado == Estado.ERRO) {
                    carregar();
                    return true;
                }
                return false;
            }
        };
    }

    @Override
    public void render(float delta) {
        iniciarDesenho(delta);
        painel(90, 30, 620, 540);
        jogo.batch().begin();
        texto(global ? "RANKING GLOBAL - TOP 10" : "RANKING LOCAL - TOP 10", 315, 540, Paleta.DESTAQUE);
        if (!resumo.isBlank()) {
            texto(resumo, 130, 505, Paleta.TEXTO);
        }
        switch (estado) {
            case CARREGANDO -> texto("Carregando ranking" + ".".repeat(1 + (int) (tempo * 3) % 3), 320, 420, Paleta.TEXTO);
            case ERRO -> {
                texto(erro, 160, 430, Paleta.DESTAQUE);
                texto("R tenta de novo. O modo convidado continua disponível.", 160, 400, Paleta.TEXTO);
            }
            case PRONTO -> {
                if (linhas.isEmpty()) {
                    texto("Sem pontuações registradas.", 300, 430, Paleta.TEXTO);
                }
                for (int i = 0; i < linhas.size(); i++) {
                    texto(linhas.get(i), 160, 460 - i * 32, i == linhaDoJogador ? Paleta.DESTAQUE : Paleta.TEXTO);
                }
                if (global && !resumo.isBlank()) {
                    texto(linhaDoJogador >= 0 ? "Você está no Top 10!" : "Você ainda não entrou no Top 10.",
                            320, 128, Paleta.DESTAQUE);
                }
            }
        }
        jogo.batch().end();
        desenharBotoes();
    }
}
