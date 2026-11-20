package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.gdx.recursos.Som;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;

final class NovaPartidaScreen extends TelaBase {
    private final StringBuilder nome = new StringBuilder();
    private Dificuldade dificuldade = Dificuldade.MEDIO;

    NovaPartidaScreen(MissaoMarteGame jogo) {
        super(jogo);
        botoes.add(new Botao("F1  Fácil", 196, 300, 132, 46, () -> escolher(Dificuldade.FACIL)));
        botoes.add(new Botao("F2  Médio", 334, 300, 132, 46, () -> escolher(Dificuldade.MEDIO)));
        botoes.add(new Botao("F3  Difícil", 472, 300, 132, 46, () -> escolher(Dificuldade.DIFICIL)));
        botoes.add(new Botao("Enter  Decolar!", 300, 214, 200, 50, this::decolar));
        botoes.add(new Botao("Esc  Voltar", 330, 154, 140, 42, jogo::abrirMenu));
    }

    /** Convidado digita o nome (a letra M faz parte dele); logado usa o nome da conta e pode usar M. */
    @Override
    protected boolean aceitaAtalhoDeSom() {
        return jogo.logado();
    }

    private String piloto() {
        if (jogo.logado()) {
            return jogo.sessao().nome();
        }
        return nome.isEmpty() ? "Piloto" : nome.toString();
    }

    /** A dificuldade escolhida fica em menta, como uma opção marcada. */
    @Override
    protected boolean destacado(Botao botao) {
        return super.destacado(botao) || botao.rotulo().endsWith(dificuldade.toString());
    }

    @Override
    protected InputProcessor criarEntrada() {
        return new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.F1 -> escolher(Dificuldade.FACIL);
                    case Input.Keys.F2 -> escolher(Dificuldade.MEDIO);
                    case Input.Keys.F3 -> escolher(Dificuldade.DIFICIL);
                    case Input.Keys.BACKSPACE -> {
                        if (!nome.isEmpty()) {
                            nome.deleteCharAt(nome.length() - 1);
                        }
                    }
                    case Input.Keys.ENTER -> decolar();
                    case Input.Keys.ESCAPE -> jogo.abrirMenu();
                    default -> {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public boolean keyTyped(char character) {
                if (tempo == 0) {
                    return true; // caractere da tecla que abriu esta tela (ex.: "1" do menu) chega depois da troca
                }
                if (!jogo.logado() && !Character.isISOControl(character) && nome.length() < 24) {
                    nome.append(character);
                }
                return true;
            }
        };
    }

    private void escolher(Dificuldade escolhida) {
        dificuldade = escolhida;
        jogo.audio().tocar(Som.SELECIONAR);
    }

    private void decolar() {
        jogo.audio().tocar(Som.SELECIONAR);
        jogo.setScreen(new PartidaScreen(jogo, piloto(), dificuldade));
    }

    @Override
    public void render(float delta) {
        iniciarDesenho(delta);
        painel(170, 120, 460, 380);
        jogo.batch().begin();
        texto("NOVA MISSÃO", 350, 470, Paleta.DESTAQUE);
        texto("Nome do piloto:", 210, 425, Paleta.TEXTO);
        if (jogo.logado()) {
            texto(jogo.sessao().nome() + "  (sua conta)", 330, 425, Paleta.DESTAQUE);
        } else {
            texto(nome + (((int) (tempo * 2)) % 2 == 0 ? "_" : ""), 330, 425, Paleta.DESTAQUE);
        }
        texto("Dificuldade:", 210, 370, Paleta.TEXTO);
        jogo.batch().end();
        desenharBotoes();
    }
}
