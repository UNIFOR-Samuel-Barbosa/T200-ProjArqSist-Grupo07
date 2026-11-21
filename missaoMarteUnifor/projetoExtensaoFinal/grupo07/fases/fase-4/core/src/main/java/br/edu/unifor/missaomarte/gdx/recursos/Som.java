package br.edu.unifor.missaomarte.gdx.recursos;

/** Catálogo dos sons. {@code musica} marca o arquivo tocado em laço como {@code Music}. */
public enum Som {
    MOVER("mover.wav", false),
    SELECIONAR("selecionar.wav", false),
    EMBARCAR("embarcar.wav", false),
    ENTREGAR("entregar.wav", false),
    COLISAO("colisao.wav", false),
    VITORIA("vitoria.wav", false),
    DERROTA("derrota.wav", false),
    MUSICA("musica.wav", true);

    public static final String PASTA = "sons/";

    private final String arquivo;
    private final boolean musica;

    Som(String arquivo, boolean musica) {
        this.arquivo = arquivo;
        this.musica = musica;
    }

    public String arquivo() {
        return arquivo;
    }

    public String caminho() {
        return PASTA + arquivo;
    }

    public boolean musica() {
        return musica;
    }
}
