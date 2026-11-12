package br.edu.unifor.missaomarte.application.ranking;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

public final class RankingService {
    private static final int LIMITE = 10;
    private final RankingRepository repositorio;

    public RankingService(RankingRepository repositorio) {
        this.repositorio = repositorio;
    }

    public List<RankingEntry> listar() {
        return ordenar(repositorio.carregar());
    }

    public boolean registrar(String piloto, int pontuacao, String dificuldade) {
        if (pontuacao <= 0) {
            return false;
        }
        List<RankingEntry> atuais = listar();
        if (atuais.size() == LIMITE && pontuacao <= atuais.get(LIMITE - 1).pontuacao()) {
            return false;
        }
        atuais = new java.util.ArrayList<>(atuais);
        atuais.add(new RankingEntry(piloto, pontuacao, dificuldade, Instant.now()));
        repositorio.salvar(ordenar(atuais).stream().limit(LIMITE).toList());
        return true;
    }

    public void limpar() {
        repositorio.limpar();
    }

    private List<RankingEntry> ordenar(List<RankingEntry> entradas) {
        return entradas.stream()
                .sorted(Comparator.comparingInt(RankingEntry::pontuacao).reversed())
                .limit(LIMITE)
                .toList();
    }
}
