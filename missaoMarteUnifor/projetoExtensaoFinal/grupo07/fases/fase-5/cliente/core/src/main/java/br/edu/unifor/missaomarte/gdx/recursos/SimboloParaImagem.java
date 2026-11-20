package br.edu.unifor.missaomarte.gdx.recursos;

/**
 * Escolhe a imagem pelo símbolo que o próprio objeto do domínio informa, sem {@code instanceof}:
 * um tipo novo no domínio só precisa de um símbolo; sem mapeamento, usa a imagem genérica.
 */
public final class SimboloParaImagem {
    private SimboloParaImagem() {
    }

    public static Imagem imagem(char simbolo) {
        return switch (simbolo) {
            case '@' -> Imagem.NAVE;
            case 'L' -> Imagem.BASE;
            case '#' -> Imagem.ASTEROIDE;
            case 'X' -> Imagem.INIMIGO;
            case 'P' -> Imagem.PROFESSOR;
            case 'E' -> Imagem.ENGENHEIRO;
            case 'T' -> Imagem.ASTRONAUTA;
            default -> Imagem.DESCONHECIDO;
        };
    }
}
