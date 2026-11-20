package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.servidor.LinhaDoRanking;
import br.edu.unifor.missaomarte.application.servidor.PartidaRegistrada;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.gdx.hud.HudRenderer;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/** Textos das listas vindas do servidor (sem libGDX, testável). */
final class Formatos {
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    private Formatos() {
    }

    static String data(Instant instante, ZoneId fuso) {
        return DATA.format(instante.atZone(fuso));
    }

    /** "FACIL" → "Fácil"; nome desconhecido aparece como veio (o servidor pode evoluir antes do cliente). */
    static String dificuldade(String nome) {
        try {
            return Dificuldade.valueOf(nome).toString();
        } catch (IllegalArgumentException desconhecida) {
            return nome;
        }
    }

    static String resultado(String nome) {
        return switch (nome) {
            case "VITORIA" -> "Vitória";
            case "DERROTA" -> "Derrota";
            default -> nome;
        };
    }

    static String linha(LinhaDoRanking linha) {
        return linha.posicao() + ". " + linha.nome() + "  -  " + linha.pontuacao() + " pts  ("
                + dificuldade(linha.dificuldade()) + ", " + HudRenderer.formatarTempo(linha.duracaoSegundos()) + ")";
    }

    /** Colunas do histórico: data, resultado, dificuldade, pontos, tempo e movimentos. */
    static String[] colunas(PartidaRegistrada partida, ZoneId fuso) {
        return new String[] {data(partida.jogadaEm(), fuso), resultado(partida.resultado()),
                dificuldade(partida.dificuldade()), String.valueOf(partida.pontuacao()),
                HudRenderer.formatarTempo(partida.duracaoSegundos()), String.valueOf(partida.movimentos())};
    }
}
