package br.edu.unifor.missaomarte.application.servidor;

import br.edu.unifor.missaomarte.application.EstadoPartida;
import br.edu.unifor.missaomarte.application.GameSession;
import br.edu.unifor.missaomarte.domain.Dificuldade;

/** O que o cliente envia ao servidor ao fim de uma partida (vitória ou derrota; abortar não é enviado). */
public record PartidaConcluida(String dificuldade, String resultado, int pontuacao, long duracaoSegundos,
                               int movimentos) {

    public static PartidaConcluida de(GameSession sessao, Dificuldade dificuldade) {
        String resultado = switch (sessao.estado()) {
            case VITORIA -> "VITORIA";
            case DERROTA -> "DERROTA";
            case EM_ANDAMENTO, ABORTADA -> throw new IllegalStateException(
                    "Só partidas vencidas ou perdidas são enviadas (estado: " + sessao.estado() + ").");
        };
        return new PartidaConcluida(dificuldade.name(), resultado, sessao.pontuacao(), sessao.duracaoSegundos(),
                sessao.movimentos());
    }

    public boolean vitoria() {
        return EstadoPartida.VITORIA.name().equals(resultado);
    }
}
