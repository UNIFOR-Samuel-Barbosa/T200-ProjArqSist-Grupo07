package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.Acao;
import com.badlogic.gdx.Input;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TecladoParaAcaoTest {
    @Test
    void mapeiaTeclasEIgnoraTeclasSemAcao() {
        assertEquals(Acao.CIMA, TecladoParaAcao.converter(Input.Keys.W));
        assertEquals(Acao.CIMA, TecladoParaAcao.converter(Input.Keys.UP));
        assertEquals(Acao.EMBARCAR, TecladoParaAcao.converter(Input.Keys.E));
        assertEquals(Acao.ABORTAR, TecladoParaAcao.converter(Input.Keys.ESCAPE));
        assertEquals(Acao.INVALIDA, TecladoParaAcao.converter(Input.Keys.SPACE));
    }
}
