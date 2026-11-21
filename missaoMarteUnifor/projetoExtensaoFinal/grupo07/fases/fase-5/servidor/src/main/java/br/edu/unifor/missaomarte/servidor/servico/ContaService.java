package br.edu.unifor.missaomarte.servidor.servico;

import br.edu.unifor.missaomarte.servidor.persistencia.Usuario;
import br.edu.unifor.missaomarte.servidor.persistencia.UsuarioRepository;
import br.edu.unifor.missaomarte.servidor.seguranca.TokenService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.Optional;

/** Cadastro e login. As validações de formato ficam nos DTOs; aqui ficam as regras de conta. */
@Service
public class ContaService {
    private final UsuarioRepository usuarios;
    private final PasswordEncoder codificador;
    private final TokenService tokens;
    private final Clock relogio;
    /** Conferido quando o nome não existe, para o tempo de resposta não revelar quais nomes existem. */
    private final String hashDeMentira;

    public ContaService(UsuarioRepository usuarios, PasswordEncoder codificador, TokenService tokens, Clock relogio) {
        this.usuarios = usuarios;
        this.codificador = codificador;
        this.tokens = tokens;
        this.relogio = relogio;
        this.hashDeMentira = codificador.encode("senha-de-ninguem-" + System.nanoTime());
    }

    @Transactional
    public Usuario cadastrar(String nome, String senha) {
        if (usuarios.existsByNomeNormalizado(Usuario.normalizar(nome))) {
            throw new NomeEmUsoException();
        }
        try {
            // saveAndFlush: se dois cadastros iguais correrem juntos, o índice único do banco barra o segundo aqui.
            return usuarios.saveAndFlush(new Usuario(nome.trim(), codificador.encode(senha), relogio.instant()));
        } catch (DataIntegrityViolationException corrida) {
            throw new NomeEmUsoException();
        }
    }

    @Transactional(readOnly = true)
    public TokenService.Emitido entrar(String nome, String senha) {
        Optional<Usuario> usuario = usuarios.findByNomeNormalizado(Usuario.normalizar(nome));
        boolean senhaConfere = codificador.matches(senha, usuario.map(Usuario::getSenhaHash).orElse(hashDeMentira));
        if (usuario.isEmpty() || !senhaConfere) {
            throw new CredenciaisInvalidasException();
        }
        return tokens.emitir(usuario.get());
    }
}
