package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.servidor.LinhaDoRanking;
import br.edu.unifor.missaomarte.application.servidor.PartidaRegistrada;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FormatosTest {
    private static final ZoneId FORTALEZA = ZoneId.of("America/Fortaleza");

    @Test
    void dataDoHistoricoNoFusoDoJogador() {
        assertEquals("20/11 20:04", Formatos.data(Instant.parse("2026-11-20T23:04:59Z"), FORTALEZA));
        assertEquals("01/01 00:00", Formatos.data(Instant.parse("2027-01-01T03:00:00Z"), FORTALEZA));
    }

    @Test
    void nomesDoServidorViramRotulosEDesconhecidoPassaComoVeio() {
        assertEquals("Fácil", Formatos.dificuldade("FACIL"));
        assertEquals("Difícil", Formatos.dificuldade("DIFICIL"));
        assertEquals("INSANO", Formatos.dificuldade("INSANO"));
        assertEquals("Vitória", Formatos.resultado("VITORIA"));
        assertEquals("Derrota", Formatos.resultado("DERROTA"));
    }

    @Test
    void linhasDoRankingEDoHistorico() {
        assertEquals("1. Lia  -  47 pts  (Fácil, 00:21)", Formatos.linha(new LinhaDoRanking(1, "Lia", 47, "FACIL", 21)));
        assertArrayEquals(new String[] {"20/11 20:04", "Derrota", "Médio", "-2", "01:05", "12"}, Formatos.colunas(
                new PartidaRegistrada(7, "MEDIO", "DERROTA", -2, 65, 12, Instant.parse("2026-11-20T23:04:00Z")), FORTALEZA));
    }
}
