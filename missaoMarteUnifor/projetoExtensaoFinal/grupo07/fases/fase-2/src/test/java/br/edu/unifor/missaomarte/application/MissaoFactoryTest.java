package br.edu.unifor.missaomarte.application;

import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.domain.ObjetoRenderizavel;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MissaoFactoryTest {
    private final MissaoFactory fabrica = new MissaoFactory();

    @Test
    void quantidadesCorrespondemAoNivelDeDificuldade() {
        var facil = fabrica.criar(Dificuldade.FACIL, new Random(1), 5);
        var medio = fabrica.criar(Dificuldade.MEDIO, new Random(2), 5);
        var dificil = fabrica.criar(Dificuldade.DIFICIL, new Random(3), 5);

        assertEquals(4, facil.totalPassageiros());
        assertEquals(facil.totalPassageiros(), facil.nave().capacidade());
        assertEquals(5, medio.totalPassageiros());
        assertEquals(medio.totalPassageiros(), medio.nave().capacidade());
        assertEquals(5, dificil.totalPassageiros());
        assertEquals(dificil.totalPassageiros(), dificil.nave().capacidade());
    }

    @Test
    void nenhumaEntidadeComecaCompartilhandoCelula() {
        var missao = fabrica.criar(Dificuldade.DIFICIL, new Random(7), 5);
        Set<?> posicoes = new HashSet<>(missao.objetosRenderizaveis().stream()
                .map(ObjetoRenderizavel::posicao).toList());

        assertEquals(missao.objetosRenderizaveis().size(), posicoes.size());
        assertTrue(missao.objetosRenderizaveis().stream()
                .noneMatch(objeto -> objeto.posicao().equals(missao.nave().posicao())
                        && objeto != missao.nave()));
    }
}
