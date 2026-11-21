package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.gdx.recursos.Imagem;
import br.edu.unifor.missaomarte.gdx.recursos.Som;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;

/**
 * Menu com dois modos, decididos por existir ou não uma sessão:
 * convidado (ranking local, como na Fase 4) e logado (ranking global e histórico no servidor).
 */
final class MenuScreen extends TelaBase {
    private final Runnable[] opcoes;
    private String mensagem;

    MenuScreen(MissaoMarteGame jogo) {
        super(jogo);
        mensagem = jogo.consumirAviso();
        if (jogo.logado()) {
            opcoes = new Runnable[] {this::novaPartida, () -> abrir(() -> jogo.abrirRanking("")),
                    () -> abrir(jogo::abrirHistorico), () -> abrir(jogo::sairDaConta), Gdx.app::exit};
            adicionarBotoes("Jogar", "Ranking global", "Meu histórico", "Sair da conta", "Sair");
        } else {
            opcoes = new Runnable[] {this::novaPartida, () -> abrir(() -> jogo.abrirRanking("")),
                    this::limparRankingLocal, () -> abrir(jogo::abrirLogin), Gdx.app::exit};
            adicionarBotoes("Jogar", "Ranking local", "Limpar ranking local", "Entrar ou criar conta", "Sair");
        }
    }

    private void adicionarBotoes(String... rotulos) {
        for (int i = 0; i < rotulos.length; i++) {
            botoes.add(new Botao((i + 1) + "  " + rotulos[i], 270, 330 - i * 52, 260, 46, opcoes[i]));
        }
    }

    @Override
    protected void aoMostrar() {
        // Volta da vitória/derrota: a música, pausada no fim da partida, é retomada.
        jogo.audio().iniciarMusica();
    }

    private void abrir(Runnable destino) {
        jogo.audio().tocar(Som.SELECIONAR);
        destino.run();
    }

    private void novaPartida() {
        abrir(jogo::abrirNovaPartida);
    }

    private void limparRankingLocal() {
        jogo.audio().tocar(Som.SELECIONAR);
        jogo.ranking().limpar();
        mensagem = "Ranking local limpo.";
    }

    @Override
    protected InputProcessor criarEntrada() {
        return new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                int indice = keycode - Input.Keys.NUM_1;
                if (indice >= 0 && indice < opcoes.length) {
                    opcoes[indice].run();
                    return true;
                }
                if (keycode == Input.Keys.ESCAPE) {
                    Gdx.app.exit();
                    return true;
                }
                return false;
            }
        };
    }

    @Override
    public void render(float delta) {
        iniciarDesenho(delta);
        painel(240, 40, 320, 530);
        jogo.batch().begin();
        float flutuar = (float) Math.sin(tempo * 2.5f) * 6;
        jogo.batch().draw(jogo.recursos().emLaco(Imagem.PROPULSOR, tempo), 380, 448 + flutuar, 40, 40);
        jogo.batch().draw(jogo.recursos().regiao(Imagem.NAVE), 368, 468 + flutuar, 64, 64);
        texto("MISSÃO MARTE", 347, 455, Paleta.DESTAQUE);
        texto(jogo.logado() ? "Piloto: " + jogo.sessao().nome() : "Modo convidado", 270, 410, Paleta.TEXTO);
        texto(mensagem, 270, 98, Paleta.DESTAQUE);
        texto(jogo.audio().mudo() ? "M: ligar som" : "M: desligar som", 345, 72, Paleta.TEXTO);
        jogo.batch().end();
        desenharBotoes();
    }
}
