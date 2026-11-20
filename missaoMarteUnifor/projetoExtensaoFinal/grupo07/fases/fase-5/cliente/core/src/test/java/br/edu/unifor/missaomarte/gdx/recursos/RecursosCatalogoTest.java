package br.edu.unifor.missaomarte.gdx.recursos;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Confere os arquivos de core/assets contra o catálogo, sem OpenGL nem dispositivo de áudio. */
class RecursosCatalogoTest {
    private static final File ASSETS = new File("assets");

    @Test
    void cadaImagemExisteComFaixaDeQuadrosDoTamanhoDoCatalogo() throws Exception {
        for (Imagem imagem : Imagem.values()) {
            File arquivo = new File(ASSETS, imagem.caminho());
            assertTrue(arquivo.isFile(), "falta " + arquivo);
            BufferedImage png = ImageIO.read(arquivo);
            assertNotNull(png, "não é imagem: " + arquivo);
            assertEquals(imagem.larguraQuadro() * imagem.quadros(), png.getWidth(), imagem + " largura");
            assertEquals(imagem.alturaQuadro(), png.getHeight(), imagem + " altura");
        }
    }

    @Test
    void cadaSomExisteEEAudioValidoComUmaUnicaMusica() throws Exception {
        for (Som som : Som.values()) {
            File arquivo = new File(ASSETS, som.caminho());
            assertTrue(arquivo.isFile(), "falta " + arquivo);
            assertNotNull(AudioSystem.getAudioFileFormat(arquivo), som + " não é áudio");
        }
        assertEquals(1, Arrays.stream(Som.values()).filter(Som::musica).count());
    }

    @Test
    void simboloDoDominioEscolheImagemEDesconhecidoUsaGenerica() {
        assertEquals(Imagem.NAVE, SimboloParaImagem.imagem('@'));
        assertEquals(Imagem.BASE, SimboloParaImagem.imagem('L'));
        assertEquals(Imagem.ASTEROIDE, SimboloParaImagem.imagem('#'));
        assertEquals(Imagem.INIMIGO, SimboloParaImagem.imagem('X'));
        assertEquals(Imagem.PROFESSOR, SimboloParaImagem.imagem('P'));
        assertEquals(Imagem.ENGENHEIRO, SimboloParaImagem.imagem('E'));
        assertEquals(Imagem.ASTRONAUTA, SimboloParaImagem.imagem('T'));
        assertEquals(Imagem.DESCONHECIDO, SimboloParaImagem.imagem('?'));
    }
}
