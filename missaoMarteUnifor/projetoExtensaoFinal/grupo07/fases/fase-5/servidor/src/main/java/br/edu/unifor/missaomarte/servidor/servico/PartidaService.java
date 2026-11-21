package br.edu.unifor.missaomarte.servidor.servico;

import br.edu.unifor.missaomarte.servidor.persistencia.Dificuldade;
import br.edu.unifor.missaomarte.servidor.persistencia.Partida;
import br.edu.unifor.missaomarte.servidor.persistencia.PartidaRepository;
import br.edu.unifor.missaomarte.servidor.persistencia.Resultado;
import br.edu.unifor.missaomarte.servidor.persistencia.UsuarioRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Registro, histórico e ranking. O servidor guarda o resultado que o cliente envia; ele não executa o
 * jogo e, portanto, não prova que a pontuação é verdadeira (só barra valores absurdos na validação).
 */
@Service
public class PartidaService {
    public static final int TAMANHO_DO_RANKING = 10;

    private final PartidaRepository partidas;
    private final UsuarioRepository usuarios;
    private final Clock relogio;

    public PartidaService(PartidaRepository partidas, UsuarioRepository usuarios, Clock relogio) {
        this.partidas = partidas;
        this.usuarios = usuarios;
        this.relogio = relogio;
    }

    /** O dono vem sempre do token (id), nunca do corpo da requisição. */
    @Transactional
    public Partida registrar(long usuarioId, Dificuldade dificuldade, Resultado resultado, int pontuacao,
                             long duracaoSegundos, int movimentos) {
        return partidas.save(new Partida(usuarios.getReferenceById(usuarioId), dificuldade, resultado, pontuacao,
                duracaoSegundos, movimentos, relogio.instant()));
    }

    @Transactional(readOnly = true)
    public List<Partida> historico(long usuarioId, int pagina, int tamanho) {
        return partidas.findByUsuarioIdOrderByJogadaEmDescIdDesc(usuarioId, PageRequest.of(pagina, tamanho));
    }

    /** Uma linha por jogador (a melhor vitória dele), no máximo 10. */
    @Transactional(readOnly = true)
    public List<Partida> ranking() {
        // Limitação conhecida: lê todas as vitórias positivas e fica com a primeira de cada jogador. Simples e fácil de
        // testar; com muitas partidas, trocar por uma subconsulta "melhor partida por usuário" no banco.
        Set<Long> jaListados = new HashSet<>();
        return partidas.vitoriasEmOrdemDeRanking().stream()
                .filter(p -> jaListados.add(p.getUsuario().getId()))
                .limit(TAMANHO_DO_RANKING)
                .toList();
    }
}
