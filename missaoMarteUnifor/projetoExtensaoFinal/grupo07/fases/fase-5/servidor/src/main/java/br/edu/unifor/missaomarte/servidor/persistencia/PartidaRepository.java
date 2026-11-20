package br.edu.unifor.missaomarte.servidor.persistencia;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PartidaRepository extends JpaRepository<Partida, Long> {
    /** Histórico de um jogador: da mais recente para a mais antiga (o id desempata o mesmo instante). */
    List<Partida> findByUsuarioIdOrderByJogadaEmDescIdDesc(Long usuarioId, Pageable pagina);

    /**
     * Vitórias com pontuação positiva já na ordem do ranking: maior pontuação, menor duração, mais antiga.
     * O serviço fica com a primeira de cada jogador.
     */
    @Query("""
            select p from Partida p join fetch p.usuario
            where p.resultado = br.edu.unifor.missaomarte.servidor.persistencia.Resultado.VITORIA
              and p.pontuacao > 0
            order by p.pontuacao desc, p.duracaoSegundos asc, p.jogadaEm asc, p.id asc
            """)
    List<Partida> vitoriasEmOrdemDeRanking();
}
