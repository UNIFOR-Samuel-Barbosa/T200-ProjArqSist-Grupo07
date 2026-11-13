package br.edu.unifor.missaomarte.gdx;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MapaGeometriaTest {
    @Test
    void converteEixoHorizontalVerticalECentroDaCelula() {
        MapaGeometria mapa = new MapaGeometria(400, 300, 34, 5);

        assertEquals(434, mapa.centroCelulaX(1));
        assertEquals(266, mapa.centroCelulaY(1));
        assertEquals(334, mapa.centroCelulaY(-1));
    }
}
