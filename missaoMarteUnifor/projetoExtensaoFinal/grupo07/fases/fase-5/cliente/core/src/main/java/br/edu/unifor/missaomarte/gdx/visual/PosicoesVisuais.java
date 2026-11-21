package br.edu.unifor.missaomarte.gdx.visual;

import br.edu.unifor.missaomarte.domain.ObjetoRenderizavel;
import br.edu.unifor.missaomarte.domain.Posicao;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Posição de desenho (fracionária, em células) de cada objeto do mapa. A regra muda a posição lógica de
 * uma vez; aqui o desenho persegue essa posição em linha reta a uma velocidade fixa, sem ultrapassar.
 * Os objetos são comparados por identidade (o domínio não muda) e os que sumiram do mapa são esquecidos.
 */
public final class PosicoesVisuais {
    public static final float CELULAS_POR_SEGUNDO = 12f;

    /** Ponto em unidades de célula; y cresce para baixo, como no domínio. */
    public record Ponto(float x, float y) {
    }

    private final float velocidade;
    private final Map<ObjetoRenderizavel, Ponto> pontos = new IdentityHashMap<>();

    public PosicoesVisuais() {
        this(CELULAS_POR_SEGUNDO);
    }

    public PosicoesVisuais(float velocidade) {
        this.velocidade = velocidade;
    }

    public void atualizar(Collection<? extends ObjetoRenderizavel> objetos, float delta) {
        Set<ObjetoRenderizavel> presentes = java.util.Collections.newSetFromMap(new IdentityHashMap<>());
        presentes.addAll(objetos);
        pontos.keySet().retainAll(presentes);
        for (ObjetoRenderizavel objeto : objetos) {
            Posicao alvo = objeto.posicao();
            Ponto atual = pontos.get(objeto);
            pontos.put(objeto, atual == null ? new Ponto(alvo.x(), alvo.y()) : perseguir(atual, alvo, delta));
        }
    }

    private Ponto perseguir(Ponto atual, Posicao alvo, float delta) {
        float dx = alvo.x() - atual.x();
        float dy = alvo.y() - atual.y();
        float distancia = (float) Math.sqrt(dx * dx + dy * dy);
        float passo = velocidade * delta;
        if (distancia <= passo || distancia == 0) {
            return new Ponto(alvo.x(), alvo.y());
        }
        return new Ponto(atual.x() + dx / distancia * passo, atual.y() + dy / distancia * passo);
    }

    /** Ponto de desenho; objeto ainda não visto fica na posição lógica. */
    public Ponto ponto(ObjetoRenderizavel objeto) {
        Ponto ponto = pontos.get(objeto);
        return ponto != null ? ponto : new Ponto(objeto.posicao().x(), objeto.posicao().y());
    }

    public int quantidade() {
        return pontos.size();
    }
}
