package br.edu.unifor.missaomarte.servidor.api.dto;

import br.edu.unifor.missaomarte.servidor.persistencia.Dificuldade;
import br.edu.unifor.missaomarte.servidor.persistencia.Partida;
import br.edu.unifor.missaomarte.servidor.persistencia.Resultado;
import br.edu.unifor.missaomarte.servidor.persistencia.Usuario;

import java.time.Instant;

/**
 * DTOs de saída. As entidades nunca saem pela API: o contrato fica estável mesmo que o banco mude,
 * e campos internos (hash da senha, nome normalizado) não vazam.
 */
public final class Respostas {
    private Respostas() {
    }

    public record UsuarioCriado(long id, String nome) {
        public static UsuarioCriado de(Usuario usuario) {
            return new UsuarioCriado(usuario.getId(), usuario.getNome());
        }
    }

    public record Token(String token, String tipo, Instant expiraEm) {
        @Override
        public String toString() {
            return "Token[token=***, tipo=" + tipo + ", expiraEm=" + expiraEm + "]";
        }
    }

    public record PartidaRegistrada(long id, Dificuldade dificuldade, Resultado resultado, int pontuacao,
                                    long duracaoSegundos, int movimentos, Instant jogadaEm) {
        public static PartidaRegistrada de(Partida partida) {
            return new PartidaRegistrada(partida.getId(), partida.getDificuldade(), partida.getResultado(),
                    partida.getPontuacao(), partida.getDuracaoSegundos(), partida.getMovimentos(), partida.getJogadaEm());
        }
    }

    public record LinhaDoRanking(int posicao, String nome, int pontuacao, Dificuldade dificuldade,
                                 long duracaoSegundos, Instant jogadaEm) {
        public static LinhaDoRanking de(int posicao, Partida partida) {
            return new LinhaDoRanking(posicao, partida.getUsuario().getNome(), partida.getPontuacao(),
                    partida.getDificuldade(), partida.getDuracaoSegundos(), partida.getJogadaEm());
        }
    }
}
