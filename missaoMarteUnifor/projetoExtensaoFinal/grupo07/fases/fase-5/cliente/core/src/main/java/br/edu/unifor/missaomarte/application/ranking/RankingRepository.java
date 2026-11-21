package br.edu.unifor.missaomarte.application.ranking;

import java.util.List;

public interface RankingRepository {
    List<RankingEntry> carregar();

    void salvar(List<RankingEntry> entradas);

    void limpar();
}
