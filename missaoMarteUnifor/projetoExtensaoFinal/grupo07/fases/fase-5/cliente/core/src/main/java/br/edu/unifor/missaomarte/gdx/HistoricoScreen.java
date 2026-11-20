package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.servidor.PartidaRegistrada;
import br.edu.unifor.missaomarte.gdx.recursos.Som;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;

import java.time.ZoneId;
import java.util.List;

/** Últimas 10 partidas do jogador logado, pedidas ao servidor com estados visíveis. */
final class HistoricoScreen extends TelaBase {
    private enum Estado { CARREGANDO, PRONTO, ERRO }

    private static final String[] CABECALHO = {"Data", "Resultado", "Dificuldade", "Pontos", "Tempo", "Movimentos"};
    private static final float[] COLUNAS_X = {110, 230, 340, 460, 550, 630};

    private List<String[]> linhas = List.of();
    private Estado estado;
    private String erro = "";

    HistoricoScreen(MissaoMarteGame jogo) {
        super(jogo);
        botoes.add(new Botao("Enter  Voltar ao menu", 290, 58, 220, 46, this::voltar));
        carregar();
    }

    private void carregar() {
        estado = Estado.CARREGANDO;
        jogo.servidor().carregarHistorico(jogo.sessao(), resposta -> {
            if (jogo.getScreen() != this) {
                return;
            }
            if (resposta.sucesso()) {
                ZoneId fuso = ZoneId.systemDefault();
                linhas = resposta.valor().stream().map((PartidaRegistrada p) -> Formatos.colunas(p, fuso)).toList();
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
                if (keycode == Input.Keys.R && estado == Estado.ERRO) {
                    carregar();
                    return true;
                }
                return false;
            }
        };
    }

    private void linha(String[] colunas, float y, com.badlogic.gdx.graphics.Color cor) {
        for (int c = 0; c < colunas.length; c++) {
            texto(colunas[c], COLUNAS_X[c], y, cor);
        }
    }

    @Override
    public void render(float delta) {
        iniciarDesenho(delta);
        painel(70, 30, 660, 540);
        jogo.batch().begin();
        texto("MEU HISTÓRICO - " + jogo.sessao().nome(), 300, 540, Paleta.DESTAQUE);
        linha(CABECALHO, 500, Paleta.DESTAQUE);
        switch (estado) {
            case CARREGANDO -> texto("Carregando histórico" + ".".repeat(1 + (int) (tempo * 3) % 3), 310, 420, Paleta.TEXTO);
            case ERRO -> {
                texto(erro, 140, 430, Paleta.DESTAQUE);
                texto("R tenta de novo.", 140, 400, Paleta.TEXTO);
            }
            case PRONTO -> {
                if (linhas.isEmpty()) {
                    texto("Nenhuma partida registrada ainda.", 280, 430, Paleta.TEXTO);
                }
                for (int i = 0; i < linhas.size(); i++) {
                    linha(linhas.get(i), 462 - i * 32, Paleta.TEXTO);
                }
            }
        }
        jogo.batch().end();
        desenharBotoes();
    }
}
