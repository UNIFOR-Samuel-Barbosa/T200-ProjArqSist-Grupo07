package br.edu.unifor.missaomarte.gdx.visual;

import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.ObjetoRenderizavel;
import br.edu.unifor.missaomarte.gdx.MapaGeometria;
import br.edu.unifor.missaomarte.gdx.Paleta;
import br.edu.unifor.missaomarte.gdx.recursos.Imagem;
import br.edu.unifor.missaomarte.gdx.recursos.Recursos;
import br.edu.unifor.missaomarte.gdx.recursos.SimboloParaImagem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.List;

/**
 * Desenha o mapa: terreno em todas as células, os objetos na ordem que o domínio informa (a nave por
 * último), os efeitos de uma execução e o flash da colisão. Só lê o estado; não decide nada.
 */
public final class MundoRenderer {
    /** O que a tela sabe sobre a nave e o que o domínio não guarda (é só aparência). */
    public record EstadoDaNave(float rotacao, boolean visivel) {
    }

    private final SpriteBatch batch;
    private final ShapeRenderer formas;
    private final Recursos recursos;
    private final MapaGeometria geometria;

    public MundoRenderer(SpriteBatch batch, ShapeRenderer formas, Recursos recursos, MapaGeometria geometria) {
        this.batch = batch;
        this.formas = formas;
        this.recursos = recursos;
        this.geometria = geometria;
    }

    public void desenhar(Missao missao, PosicoesVisuais posicoes, EstadoDaNave nave, float tempo,
                         List<EfeitoUnico> efeitos, float alfaFlash) {
        float lado = geometria.tamanhoCelula();
        int raio = geometria.raio();
        batch.begin();
        batch.setColor(1, 1, 1, 1);
        for (int x = -raio; x <= raio; x++) {
            for (int y = -raio; y <= raio; y++) {
                desenharNaCelula(recursos.regiao(Imagem.TERRENO), x, y, lado, 0);
            }
        }
        for (ObjetoRenderizavel objeto : missao.objetosRenderizaveis()) {
            PosicoesVisuais.Ponto ponto = posicoes.ponto(objeto);
            Imagem imagem = SimboloParaImagem.imagem(objeto.simbolo());
            if (imagem == Imagem.NAVE) {
                if (nave.visivel()) {
                    desenharPropulsor(ponto, lado, nave.rotacao(), tempo);
                    desenharNaCelula(recursos.regiao(Imagem.NAVE), ponto.x(), ponto.y(), lado, nave.rotacao());
                }
            } else {
                // Leve balanço vertical: só aparência, não muda a célula lógica.
                float balanco = (float) Math.sin(tempo * 3 + ponto.x()) * 0.04f;
                desenharNaCelula(recursos.emLaco(imagem, tempo), ponto.x(), ponto.y() + balanco, lado * 0.92f, 0);
            }
        }
        for (EfeitoUnico efeito : efeitos) {
            desenharNaCelula(recursos.quadro(efeito.imagem(), efeito.quadroAtual()), efeito.x(), efeito.y(), lado * 1.5f, 0);
        }
        batch.end();

        if (alfaFlash > 0) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            formas.begin(ShapeRenderer.ShapeType.Filled);
            formas.setColor(Paleta.FLASH.r, Paleta.FLASH.g, Paleta.FLASH.b, alfaFlash);
            float metade = (raio + 0.5f) * lado;
            formas.rect(geometria.centroCelulaX(0) - metade, geometria.centroCelulaY(0) - metade, metade * 2, metade * 2);
            formas.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    /** Chama atrás da nave: desenhada abaixo do centro e girada em torno do centro da célula. */
    private void desenharPropulsor(PosicoesVisuais.Ponto ponto, float lado, float rotacao, float tempo) {
        float tamanho = lado * 0.6f;
        float recuo = lado * 0.62f;
        float cx = geometria.centroCelulaX(ponto.x());
        float cy = geometria.centroCelulaY(ponto.y());
        batch.draw(recursos.emLaco(Imagem.PROPULSOR, tempo), cx - tamanho / 2, cy - recuo - tamanho / 2,
                tamanho / 2, recuo + tamanho / 2, tamanho, tamanho, 1, 1, rotacao);
    }

    private void desenharNaCelula(TextureRegion regiao, float x, float y, float tamanho, float rotacao) {
        float cx = geometria.centroCelulaX(x);
        float cy = geometria.centroCelulaY(y);
        batch.draw(regiao, cx - tamanho / 2, cy - tamanho / 2, tamanho / 2, tamanho / 2,
                tamanho, tamanho, 1, 1, rotacao);
    }
}
