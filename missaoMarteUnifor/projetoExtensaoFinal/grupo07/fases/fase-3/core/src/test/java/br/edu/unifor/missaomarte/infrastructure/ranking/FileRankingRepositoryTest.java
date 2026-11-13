package br.edu.unifor.missaomarte.infrastructure.ranking;

import br.edu.unifor.missaomarte.application.ranking.RankingEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileRankingRepositoryTest {
    @TempDir
    Path pastaTemporaria;

    @Test
    void salvaERecuperaRankingSemPerderCaracteresEspeciais() {
        FileRankingRepository repositorio = new FileRankingRepository(pastaTemporaria.resolve("ranking.json"));
        List<RankingEntry> registros = List.of(
                new RankingEntry("Ana \"Marte\"\nEquipe", 87, "Fácil", Instant.parse("2026-11-12T12:00:00Z")));

        repositorio.salvar(registros);

        assertEquals(registros, repositorio.carregar());
    }

    @Test
    void carregaRankingVazioGravadoComArrayFormatado() {
        FileRankingRepository repositorio = new FileRankingRepository(pastaTemporaria.resolve("ranking.json"));

        repositorio.salvar(List.of());

        assertEquals(List.of(), repositorio.carregar());
    }

    @Test
    void rejeitaArquivoComFormatoInvalido() throws IOException {
        Path arquivo = pastaTemporaria.resolve("ranking-invalido.json");
        Files.writeString(arquivo, "{invalido}");

        assertThrows(IllegalStateException.class, () -> new FileRankingRepository(arquivo).carregar());
    }
}
