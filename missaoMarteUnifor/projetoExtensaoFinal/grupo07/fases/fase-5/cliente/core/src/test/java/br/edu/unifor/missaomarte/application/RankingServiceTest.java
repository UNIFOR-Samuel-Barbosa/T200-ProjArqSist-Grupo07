package br.edu.unifor.missaomarte.application;

import br.edu.unifor.missaomarte.application.ranking.RankingEntry;
import br.edu.unifor.missaomarte.application.ranking.RankingRepository;
import br.edu.unifor.missaomarte.application.ranking.RankingService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RankingServiceTest {
    @Test
    void guardaTopDezOrdenadoERecusaPontuacaoQueNaoSuperaACorte() {
        InMemoryRankingRepository memoria = new InMemoryRankingRepository();
        RankingService servico = new RankingService(memoria);
        for (int pontos = 1; pontos <= 10; pontos++) {
            assertTrue(servico.registrar("Piloto " + pontos, pontos * 10, "Médio"));
        }

        assertFalse(servico.registrar("Sem pontos", 0, "Fácil"));
        assertFalse(servico.registrar("Corte", 10, "Fácil"));
        assertTrue(servico.registrar("Novo recorde", 110, "Difícil"));

        assertEquals(10, servico.listar().size());
        assertEquals(110, servico.listar().get(0).pontuacao());
        assertEquals(20, servico.listar().get(9).pontuacao());
        servico.limpar();
        assertTrue(servico.listar().isEmpty());
    }

    private static final class InMemoryRankingRepository implements RankingRepository {
        private List<RankingEntry> entradas = new ArrayList<>();

        @Override
        public List<RankingEntry> carregar() {
            return List.copyOf(entradas);
        }

        @Override
        public void salvar(List<RankingEntry> novasEntradas) {
            entradas = new ArrayList<>(novasEntradas);
        }

        @Override
        public void limpar() {
            entradas.clear();
        }
    }
}
