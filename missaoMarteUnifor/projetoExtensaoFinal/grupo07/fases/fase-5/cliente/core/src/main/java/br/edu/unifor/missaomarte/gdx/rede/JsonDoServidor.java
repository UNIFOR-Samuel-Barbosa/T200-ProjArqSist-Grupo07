package br.edu.unifor.missaomarte.gdx.rede;

import br.edu.unifor.missaomarte.application.servidor.LinhaDoRanking;
import br.edu.unifor.missaomarte.application.servidor.PartidaConcluida;
import br.edu.unifor.missaomarte.application.servidor.PartidaRegistrada;
import br.edu.unifor.missaomarte.application.servidor.Resposta;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Só converte JSON (objeto → texto, texto → objeto) e traduz respostas em {@link Resposta}.
 * Não usa rede nem OpenGL: {@code JsonWriter}/{@code JsonReader} da libGDX são Java puro e testáveis.
 * O JSON enviado é montado pelo {@code JsonWriter}, que escapa aspas e barras; nunca por concatenação.
 */
public final class JsonDoServidor {
    static final String SEM_CONEXAO = "Não foi possível conectar ao servidor.";
    static final String RESPOSTA_INVALIDA = "Resposta inválida do servidor.";
    static final String SESSAO_EXPIRADA = "Sua sessão expirou. Entre novamente.";

    private JsonDoServidor() {
    }

    // ------------------------------------------------------------- escrita

    public static String credenciais(String nome, String senha) {
        return escrever(w -> w.set("nome", nome).set("senha", senha));
    }

    public static String partida(PartidaConcluida partida) {
        return escrever(w -> w.set("dificuldade", partida.dificuldade())
                .set("resultado", partida.resultado())
                .set("pontuacao", partida.pontuacao())
                .set("duracaoSegundos", partida.duracaoSegundos())
                .set("movimentos", partida.movimentos()));
    }

    private interface Campos {
        void escrever(JsonWriter escritor) throws IOException;
    }

    private static String escrever(Campos campos) {
        StringWriter texto = new StringWriter();
        JsonWriter escritor = new JsonWriter(texto);
        escritor.setOutputType(JsonWriter.OutputType.json);
        try {
            escritor.object();
            campos.escrever(escritor);
            escritor.pop();
            escritor.close();
        } catch (IOException impossivelEmMemoria) {
            throw new UncheckedIOException(impossivelEmMemoria);
        }
        return texto.toString();
    }

    // ------------------------------------------------------------- leitura

    public static String nomeCadastrado(String json) {
        return new JsonReader().parse(json).getString("nome");
    }

    /** Lê o token do login; falha (IllegalArgumentException) se o campo não vier. */
    public static String token(String json) {
        return new JsonReader().parse(json).getString("token");
    }

    public static PartidaRegistrada partidaRegistrada(String json) {
        return partidaRegistrada(new JsonReader().parse(json));
    }

    public static List<PartidaRegistrada> historico(String json) {
        List<PartidaRegistrada> partidas = new ArrayList<>();
        for (JsonValue item = lista(json).child; item != null; item = item.next) {
            partidas.add(partidaRegistrada(item));
        }
        return partidas;
    }

    public static List<LinhaDoRanking> ranking(String json) {
        List<LinhaDoRanking> linhas = new ArrayList<>();
        for (JsonValue item = lista(json).child; item != null; item = item.next) {
            linhas.add(new LinhaDoRanking(item.getInt("posicao"), item.getString("nome"), item.getInt("pontuacao"),
                    item.getString("dificuldade"), item.getLong("duracaoSegundos")));
        }
        return linhas;
    }

    private static JsonValue lista(String json) {
        JsonValue raiz = new JsonReader().parse(json);
        if (raiz == null || !raiz.isArray()) {
            throw new IllegalArgumentException("Era esperada uma lista.");
        }
        return raiz;
    }

    private static PartidaRegistrada partidaRegistrada(JsonValue item) {
        return new PartidaRegistrada(item.getLong("id"), item.getString("dificuldade"), item.getString("resultado"),
                item.getInt("pontuacao"), item.getLong("duracaoSegundos"), item.getInt("movimentos"),
                Instant.parse(item.getString("jogadaEm")));
    }

    // ------------------------------------------------------------- respostas e erros

    /**
     * Traduz status e corpo em {@link Resposta}. 2xx passa pelo conversor (corpo estranho vira erro, não
     * exceção); 401 em rota protegida marca sessão expirada; o resto vira uma mensagem segura.
     */
    public static <T> Resposta<T> interpretar(int status, String corpo, boolean rotaProtegida, Function<String, T> conversor) {
        if (status <= 0) {
            // Achado na integração: com o servidor desligado o backend desktop entrega status -1 (não chama failed).
            return Resposta.falha(SEM_CONEXAO);
        }
        if (status >= 200 && status < 300) {
            try {
                return Resposta.ok(conversor.apply(corpo));
            } catch (RuntimeException corpoInesperado) {
                return Resposta.falha(RESPOSTA_INVALIDA);
            }
        }
        if (status == 401 && rotaProtegida) {
            return Resposta.naoAutorizado(SESSAO_EXPIRADA);
        }
        return Resposta.falha(mensagemDeErro(status, corpo));
    }

    /**
     * Mensagem para o jogador a partir do ProblemDetail: primeiro erro de campo, senão {@code detail},
     * senão um texto padrão pelo código. O corpo bruto nunca é mostrado.
     */
    public static String mensagemDeErro(int status, String corpo) {
        try {
            JsonValue problema = new JsonReader().parse(corpo);
            if (problema != null && problema.isObject()) {
                JsonValue erros = problema.get("erros");
                if (erros != null && erros.child != null && erros.child.isString()) {
                    return erros.child.asString();
                }
                if (problema.has("detail") && problema.get("detail").isString()) {
                    return problema.getString("detail");
                }
            }
        } catch (RuntimeException naoEraJson) {
            // cai no texto padrão
        }
        if (status == 401) {
            return "Usuário ou senha inválidos.";
        }
        if (status >= 500) {
            return "O servidor teve um problema. Tente de novo mais tarde.";
        }
        return "O servidor recusou a operação (código " + status + ").";
    }
}
