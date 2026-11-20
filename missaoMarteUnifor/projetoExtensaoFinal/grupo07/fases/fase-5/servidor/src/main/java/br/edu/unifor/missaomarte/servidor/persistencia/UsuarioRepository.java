package br.edu.unifor.missaomarte.servidor.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByNomeNormalizado(String nomeNormalizado);

    boolean existsByNomeNormalizado(String nomeNormalizado);
}
