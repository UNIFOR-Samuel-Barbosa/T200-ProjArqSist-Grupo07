package br.edu.unifor.missaomarte.console;

import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.ObjetoRenderizavel;
import br.edu.unifor.missaomarte.domain.Posicao;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ConsoleRenderer {
    public void desenhar(Missao missao) {
        Map<Character, String> legenda = new LinkedHashMap<>();
        for (ObjetoRenderizavel objeto : missao.objetosRenderizaveis()) {
            legenda.putIfAbsent(objeto.simbolo(), objeto.getClass().getSimpleName());
        }
        System.out.println("\nMapa da missão (nave " + missao.nave().posicao() + ")");
        for (int y = -missao.limite(); y <= missao.limite(); y++) {
            for (int x = -missao.limite(); x <= missao.limite(); x++) {
                System.out.print(simboloNa(missao, new Posicao(x, y)) + " ");
            }
            System.out.println();
        }
        System.out.print("Legenda: . = vazio");
        legenda.forEach((simbolo, tipo) -> System.out.print(" | " + simbolo + " = " + tipo));
        System.out.println();
    }

    private char simboloNa(Missao missao, Posicao posicao) {
        return missao.objetosRenderizaveis().stream()
                .filter(objeto -> objeto.posicao().equals(posicao))
                .map(ObjetoRenderizavel::simbolo)
                .reduce((anterior, atual) -> atual)
                .orElse('.');
    }
}
