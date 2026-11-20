package br.edu.unifor.missaomarte.servidor.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

/** Resultado de uma partida enviado pelo cliente e associado ao dono do token. */
@Entity
@Table(name = "partidas", indexes = @Index(name = "idx_partidas_usuario", columnList = "usuario_id"))
public class Partida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Dificuldade dificuldade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Resultado resultado;

    @Column(nullable = false)
    private int pontuacao;

    @Column(name = "duracao_segundos", nullable = false)
    private long duracaoSegundos;

    @Column(nullable = false)
    private int movimentos;

    @Column(name = "jogada_em", nullable = false)
    private Instant jogadaEm;

    protected Partida() {
        // exigido pelo JPA
    }

    public Partida(Usuario usuario, Dificuldade dificuldade, Resultado resultado, int pontuacao,
                   long duracaoSegundos, int movimentos, Instant jogadaEm) {
        this.usuario = usuario;
        this.dificuldade = dificuldade;
        this.resultado = resultado;
        this.pontuacao = pontuacao;
        this.duracaoSegundos = duracaoSegundos;
        this.movimentos = movimentos;
        this.jogadaEm = jogadaEm;
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Dificuldade getDificuldade() {
        return dificuldade;
    }

    public Resultado getResultado() {
        return resultado;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public long getDuracaoSegundos() {
        return duracaoSegundos;
    }

    public int getMovimentos() {
        return movimentos;
    }

    public Instant getJogadaEm() {
        return jogadaEm;
    }
}
