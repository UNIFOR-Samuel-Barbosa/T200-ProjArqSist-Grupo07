package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.gdx.recursos.Som;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;

/**
 * Entrar ou criar conta. O formulário (regras de digitação e validação) é {@link FormularioDeLogin};
 * esta tela só desenha e encaminha. A senha aparece mascarada e é apagada ao sair da tela.
 * Ao criar a conta, o jogo entra sozinho. A letra M é texto aqui: a tela não usa o atalho de som.
 */
final class LoginScreen extends TelaBase {
    private final FormularioDeLogin formulario = new FormularioDeLogin();
    private boolean aguardando;
    private String aviso = "";

    LoginScreen(MissaoMarteGame jogo) {
        super(jogo);
        botoes.add(new Botao("F2  Entrar / Criar conta", 250, 196, 300, 46, this::alternarModo));
        botoes.add(new Botao("Enter  Confirmar", 250, 142, 300, 46, this::confirmar));
        botoes.add(new Botao("Esc  Voltar", 330, 92, 140, 42, this::voltar));
    }

    @Override
    protected boolean aceitaAtalhoDeSom() {
        return false;
    }

    private void alternarModo() {
        formulario.alternarModo();
        aviso = "";
        jogo.audio().tocar(Som.SELECIONAR);
    }

    private void voltar() {
        formulario.limparSenha();
        jogo.abrirMenu();
    }

    private void confirmar() {
        if (aguardando) {
            return;
        }
        String problema = formulario.validar();
        if (problema != null) {
            aviso = problema;
            return;
        }
        aguardando = true;
        aviso = "Conectando ao servidor...";
        String nome = formulario.nome();
        String senha = formulario.senha();
        if (formulario.modo() == FormularioDeLogin.Modo.CRIAR_CONTA) {
            jogo.servidor().cadastrar(nome, senha, resposta -> {
                if (jogo.getScreen() != this) {
                    return;
                }
                if (resposta.sucesso()) {
                    entrar(nome, senha);
                } else {
                    falhou(resposta.erro());
                }
            });
        } else {
            entrar(nome, senha);
        }
    }

    private void entrar(String nome, String senha) {
        jogo.servidor().entrar(nome, senha, resposta -> {
            if (jogo.getScreen() != this) {
                return;
            }
            if (resposta.sucesso()) {
                formulario.limparSenha();
                jogo.entrou(resposta.valor());
            } else {
                falhou(resposta.erro());
            }
        });
    }

    private void falhou(String mensagem) {
        aguardando = false;
        aviso = mensagem;
    }

    @Override
    protected InputProcessor criarEntrada() {
        return new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.TAB, Input.Keys.UP, Input.Keys.DOWN -> formulario.alternarCampo();
                    case Input.Keys.F2 -> alternarModo();
                    case Input.Keys.BACKSPACE -> formulario.apagar();
                    case Input.Keys.ENTER -> confirmar();
                    case Input.Keys.ESCAPE -> voltar();
                    default -> {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public boolean keyTyped(char caractere) {
                if (tempo == 0) {
                    return true; // caractere da tecla que abriu esta tela (ex.: "1" do menu) chega depois da troca
                }
                if (!aguardando && caractere >= ' ') {
                    formulario.digitar(caractere);
                }
                return true;
            }
        };
    }

    @Override
    public void hide() {
        formulario.limparSenha();
        super.hide();
    }

    @Override
    public void render(float delta) {
        iniciarDesenho(delta);
        painel(200, 70, 400, 450);
        boolean criar = formulario.modo() == FormularioDeLogin.Modo.CRIAR_CONTA;
        boolean piscar = ((int) (tempo * 2)) % 2 == 0;
        jogo.batch().begin();
        texto(criar ? "CRIAR CONTA" : "ENTRAR", criar ? 350 : 368, 490, Paleta.DESTAQUE);
        campo("Nome", formulario.nome(), FormularioDeLogin.Campo.NOME, 430, piscar);
        campo("Senha", formulario.senhaMascarada(), FormularioDeLogin.Campo.SENHA, 360, piscar);
        texto("Tab ou setas trocam de campo", 290, 300, Paleta.TEXTO);
        if (criar) {
            texto("Nome: 3 a 20 letras, números, _ ou -.  Senha: 8+", 230, 275, Paleta.TEXTO);
        }
        jogo.fonte().setColor(Paleta.DESTAQUE);
        jogo.fonte().draw(jogo.batch(), aviso, 230, 260, 340, com.badlogic.gdx.utils.Align.center, true);
        jogo.batch().end();
        desenharBotoes();
    }

    private void campo(String rotulo, String valor, FormularioDeLogin.Campo campo, float y, boolean piscar) {
        boolean ativo = formulario.campo() == campo;
        texto((ativo ? "> " : "  ") + rotulo + ":", 240, y, ativo ? Paleta.DESTAQUE : Paleta.TEXTO);
        texto(valor + (ativo && piscar && !aguardando ? "_" : ""), 340, y, Paleta.TEXTO);
    }
}
