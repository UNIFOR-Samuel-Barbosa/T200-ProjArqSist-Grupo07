package br.edu.unifor.missaomarte.servidor.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Locale;

/** Jogador. Guarda só o hash da senha. O nome normalizado é único no banco (sem diferenciar caixa). */
@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String nome;

    @Column(name = "nome_normalizado", nullable = false, length = 20, unique = true)
    private String nomeNormalizado;

    @Column(name = "senha_hash", nullable = false, length = 100)
    private String senhaHash;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    protected Usuario() {
        // exigido pelo JPA
    }

    public Usuario(String nome, String senhaHash, Instant criadoEm) {
        this.nome = nome;
        this.nomeNormalizado = normalizar(nome);
        this.senhaHash = senhaHash;
        this.criadoEm = criadoEm;
    }

    public static String normalizar(String nome) {
        return nome.trim().toLowerCase(Locale.ROOT);
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }
}
