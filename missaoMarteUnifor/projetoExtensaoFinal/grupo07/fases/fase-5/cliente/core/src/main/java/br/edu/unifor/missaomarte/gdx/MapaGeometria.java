package br.edu.unifor.missaomarte.gdx;

/** Converte células do domínio (inteiras ou fracionárias, y para baixo) em coordenadas de tela (y para cima). */
public final class MapaGeometria {
    private final float centroX;
    private final float topoY;
    private final float tamanhoCelula;
    private final int raio;

    public MapaGeometria(float centroX, float topoY, float tamanhoCelula, int raio) {
        this.centroX = centroX;
        this.topoY = topoY;
        this.tamanhoCelula = tamanhoCelula;
        this.raio = raio;
    }

    public float centroCelulaX(float x) {
        return centroX + x * tamanhoCelula;
    }

    public float centroCelulaY(float y) {
        return topoY - y * tamanhoCelula;
    }

    public float tamanhoCelula() {
        return tamanhoCelula;
    }

    public int raio() {
        return raio;
    }
}
