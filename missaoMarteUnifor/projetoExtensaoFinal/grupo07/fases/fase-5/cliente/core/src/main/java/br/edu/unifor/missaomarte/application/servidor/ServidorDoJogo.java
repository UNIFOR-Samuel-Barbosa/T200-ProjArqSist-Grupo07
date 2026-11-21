package br.edu.unifor.missaomarte.application.servidor;

import java.util.List;
import java.util.function.Consumer;

/**
 * Porta (Port and Adapter) para o servidor de ranking. A aplicação conhece só esta interface; o adaptador
 * HTTP fica em {@code gdx.rede}. Todas as operações são assíncronas: retornam na hora e entregam a
 * {@link Resposta} depois, pelo callback, na thread da interface.
 */
public interface ServidorDoJogo {
    void cadastrar(String nome, String senha, Consumer<Resposta<String>> aoTerminar);

    void entrar(String nome, String senha, Consumer<Resposta<Sessao>> aoTerminar);

    void enviarPartida(Sessao sessao, PartidaConcluida partida, Consumer<Resposta<PartidaRegistrada>> aoTerminar);

    void carregarRanking(Sessao sessao, Consumer<Resposta<List<LinhaDoRanking>>> aoTerminar);

    void carregarHistorico(Sessao sessao, Consumer<Resposta<List<PartidaRegistrada>>> aoTerminar);
}
