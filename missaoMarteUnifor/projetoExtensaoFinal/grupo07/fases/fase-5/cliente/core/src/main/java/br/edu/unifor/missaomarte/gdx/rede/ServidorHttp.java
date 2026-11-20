package br.edu.unifor.missaomarte.gdx.rede;

import br.edu.unifor.missaomarte.application.servidor.LinhaDoRanking;
import br.edu.unifor.missaomarte.application.servidor.PartidaConcluida;
import br.edu.unifor.missaomarte.application.servidor.PartidaRegistrada;
import br.edu.unifor.missaomarte.application.servidor.Resposta;
import br.edu.unifor.missaomarte.application.servidor.ServidorDoJogo;
import br.edu.unifor.missaomarte.application.servidor.Sessao;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Adaptador HTTP da porta {@link ServidorDoJogo} com {@code Gdx.net}. {@code sendHttpRequest} não bloqueia:
 * a resposta chega em outra thread, e todo resultado volta à thread de desenho por {@code postRunnable},
 * onde as telas podem mexer no próprio estado com segurança. Nada aqui registra senha ou token em log.
 */
public final class ServidorHttp implements ServidorDoJogo {
    private static final int TEMPO_LIMITE_MS = 5000;

    private final String endereco;

    public ServidorHttp(String endereco) {
        this.endereco = endereco.endsWith("/") ? endereco.substring(0, endereco.length() - 1) : endereco;
    }

    @Override
    public void cadastrar(String nome, String senha, Consumer<Resposta<String>> aoTerminar) {
        chamar(Net.HttpMethods.POST, "/api/usuarios", JsonDoServidor.credenciais(nome, senha), null,
                JsonDoServidor::nomeCadastrado, aoTerminar);
    }

    @Override
    public void entrar(String nome, String senha, Consumer<Resposta<Sessao>> aoTerminar) {
        chamar(Net.HttpMethods.POST, "/api/auth/login", JsonDoServidor.credenciais(nome, senha), null,
                corpo -> new Sessao(nome, JsonDoServidor.token(corpo)), aoTerminar);
    }

    @Override
    public void enviarPartida(Sessao sessao, PartidaConcluida partida, Consumer<Resposta<PartidaRegistrada>> aoTerminar) {
        chamar(Net.HttpMethods.POST, "/api/partidas", JsonDoServidor.partida(partida), sessao,
                JsonDoServidor::partidaRegistrada, aoTerminar);
    }

    @Override
    public void carregarRanking(Sessao sessao, Consumer<Resposta<List<LinhaDoRanking>>> aoTerminar) {
        chamar(Net.HttpMethods.GET, "/api/ranking", null, sessao, JsonDoServidor::ranking, aoTerminar);
    }

    @Override
    public void carregarHistorico(Sessao sessao, Consumer<Resposta<List<PartidaRegistrada>>> aoTerminar) {
        chamar(Net.HttpMethods.GET, "/api/partidas/minhas?pagina=0&tamanho=10", null, sessao,
                JsonDoServidor::historico, aoTerminar);
    }

    private <T> void chamar(String metodo, String rota, String corpo, Sessao sessao, Function<String, T> conversor,
                            Consumer<Resposta<T>> aoTerminar) {
        HttpRequestBuilder construtor = new HttpRequestBuilder().newRequest()
                .method(metodo)
                .url(endereco + rota)
                .timeout(TEMPO_LIMITE_MS)
                .header("Accept", "application/json");
        if (corpo != null) {
            construtor.header("Content-Type", "application/json").content(corpo);
        }
        if (sessao != null) {
            construtor.header("Authorization", "Bearer " + sessao.token());
        }
        Gdx.net.sendHttpRequest(construtor.build(), new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse resposta) {
                int status = resposta.getStatus().getStatusCode();
                String texto = resposta.getResultAsString();
                entregar(aoTerminar, JsonDoServidor.interpretar(status, texto, sessao != null, conversor));
            }

            @Override
            public void failed(Throwable motivo) {
                entregar(aoTerminar, Resposta.falha(JsonDoServidor.SEM_CONEXAO));
            }

            @Override
            public void cancelled() {
                entregar(aoTerminar, Resposta.falha(JsonDoServidor.SEM_CONEXAO));
            }
        });
    }

    private static <T> void entregar(Consumer<Resposta<T>> aoTerminar, Resposta<T> resposta) {
        Gdx.app.postRunnable(() -> aoTerminar.accept(resposta));
    }
}
