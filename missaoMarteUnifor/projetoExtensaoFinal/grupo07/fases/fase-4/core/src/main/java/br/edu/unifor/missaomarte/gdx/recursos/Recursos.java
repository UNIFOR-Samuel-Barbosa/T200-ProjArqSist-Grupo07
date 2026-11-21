package br.edu.unifor.missaomarte.gdx.recursos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

import java.util.EnumMap;
import java.util.Map;

/**
 * Carrega todas as imagens e sons uma única vez pelo {@link AssetManager} (campo de instância, nunca
 * estático: o contexto OpenGL pode ser recriado) e libera tudo em {@link #dispose()}.
 * As telas pedem regiões e animações daqui; nenhuma tela cria ou libera textura.
 */
public final class Recursos implements Disposable {
    private static final float SEGUNDOS_POR_QUADRO = 0.18f;

    private final AssetManager assets = new AssetManager();
    private final boolean audioAtivo;
    private final Map<Imagem, Animation<TextureRegion>> animacoes = new EnumMap<>(Imagem.class);
    private final NinePatch painel;

    public Recursos(boolean audioAtivo) {
        this.audioAtivo = audioAtivo;
        for (Imagem imagem : Imagem.values()) {
            assets.load(imagem.caminho(), Texture.class);
        }
        if (audioAtivo) {
            for (Som som : Som.values()) {
                if (som.musica()) {
                    assets.load(som.caminho(), Music.class);
                } else {
                    assets.load(som.caminho(), Sound.class);
                }
            }
        }
        // load() só enfileira; a carga acontece aqui.
        assets.finishLoading();

        for (Imagem imagem : Imagem.values()) {
            Texture textura = assets.get(imagem.caminho(), Texture.class);
            textura.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            TextureRegion[] quadros = TextureRegion.split(textura, imagem.larguraQuadro(), imagem.alturaQuadro())[0];
            animacoes.put(imagem, new Animation<>(SEGUNDOS_POR_QUADRO, quadros));
        }
        // Painel em nove partes: os cantos não deformam ao esticar; escala 0,4 deixa a moldura delicada.
        painel = new NinePatch(regiao(Imagem.PAINEL), 52, 52, 52, 52);
        painel.scale(0.4f, 0.4f);
        Gdx.app.log("Recursos", Imagem.values().length + " imagens e "
                + (audioAtivo ? Som.values().length : 0) + " sons carregados");
    }

    public TextureRegion regiao(Imagem imagem) {
        return animacoes.get(imagem).getKeyFrames()[0];
    }

    /** Quadro de uma animação em laço (propulsor, base, inimigo). */
    public TextureRegion emLaco(Imagem imagem, float tempo) {
        return animacoes.get(imagem).getKeyFrame(tempo, true);
    }

    /** Quadro pelo índice, usado pelos efeitos de uma execução, que controlam o próprio tempo. */
    public TextureRegion quadro(Imagem imagem, int indice) {
        TextureRegion[] quadros = animacoes.get(imagem).getKeyFrames();
        return quadros[Math.min(indice, quadros.length - 1)];
    }

    public NinePatch painel() {
        return painel;
    }

    public boolean audioAtivo() {
        return audioAtivo;
    }

    public Sound som(Som som) {
        return assets.get(som.caminho(), Sound.class);
    }

    public Music musica(Som som) {
        return assets.get(som.caminho(), Music.class);
    }

    @Override
    public void dispose() {
        assets.dispose();
    }
}
