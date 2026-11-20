package br.edu.unifor.missaomarte.gdx.audio;

import br.edu.unifor.missaomarte.gdx.recursos.Recursos;
import br.edu.unifor.missaomarte.gdx.recursos.Som;
import com.badlogic.gdx.audio.Music;

/**
 * Toca efeitos e a música e controla o mudo. Não carrega nem libera arquivos (isso é de {@link Recursos}).
 * Com o áudio desativado (máquina sem dispositivo de som) todas as operações não fazem nada.
 */
public final class Audio {
    private final Recursos recursos;
    private final Music musica;
    private boolean mudo;

    public Audio(Recursos recursos) {
        this.recursos = recursos;
        this.musica = recursos.audioAtivo() ? recursos.musica(Som.MUSICA) : null;
        if (musica != null) {
            musica.setLooping(true);
            musica.setVolume(0.45f);
        }
    }

    public void tocar(Som som) {
        if (musica != null && !mudo && !som.musica()) {
            recursos.som(som).play(0.8f);
        }
    }

    public void iniciarMusica() {
        if (musica != null && !mudo && !musica.isPlaying()) {
            musica.play();
        }
    }

    public void pausarMusica() {
        if (musica != null) {
            musica.pause();
        }
    }

    public void alternarMudo() {
        mudo = !mudo;
        if (mudo) {
            pausarMusica();
        } else {
            iniciarMusica();
        }
    }

    /** Verdadeiro também quando o áudio está desativado, para o HUD mostrar "som desligado". */
    public boolean mudo() {
        return mudo || musica == null;
    }
}
