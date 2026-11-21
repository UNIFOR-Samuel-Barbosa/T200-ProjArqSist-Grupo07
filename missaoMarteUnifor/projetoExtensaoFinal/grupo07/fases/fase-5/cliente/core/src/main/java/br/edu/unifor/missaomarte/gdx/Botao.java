package br.edu.unifor.missaomarte.gdx;

/**
 * Botão clicável de uma tela: retângulo em coordenadas do mundo (800x600) e a ação a executar.
 * Sem libGDX, para o teste de clique não precisar de janela. A mesma ação também é ligada a uma tecla.
 */
record Botao(String rotulo, float x, float y, float largura, float altura, Runnable acao) {
    boolean contem(float px, float py) {
        return px >= x && px <= x + largura && py >= y && py <= y + altura;
    }
}
