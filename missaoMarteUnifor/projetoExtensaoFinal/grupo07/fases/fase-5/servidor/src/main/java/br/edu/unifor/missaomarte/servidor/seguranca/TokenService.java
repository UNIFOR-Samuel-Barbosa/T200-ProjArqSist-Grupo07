package br.edu.unifor.missaomarte.servidor.seguranca;

import br.edu.unifor.missaomarte.servidor.persistencia.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

/** Emite o JWT: id do usuário em "sub", nome para exibição, emissão e expiração. Nunca a senha. */
@Service
public class TokenService {
    private final JwtEncoder codificador;
    private final Duration validade;
    private final Clock relogio;

    public TokenService(JwtEncoder codificador, @Value("${missaomarte.jwt.validade:2h}") Duration validade, Clock relogio) {
        this.codificador = codificador;
        this.validade = validade;
        this.relogio = relogio;
    }

    public Emitido emitir(Usuario usuario) {
        Instant agora = relogio.instant();
        return emitir(usuario, agora, agora.plus(validade));
    }

    /** Separado para os testes poderem criar um token já expirado. */
    public Emitido emitir(Usuario usuario, Instant emitidoEm, Instant expiraEm) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("missao-marte")
                .subject(String.valueOf(usuario.getId()))
                .claim("nome", usuario.getNome())
                .issuedAt(emitidoEm)
                .expiresAt(expiraEm)
                .build();
        String token = codificador.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
                .getTokenValue();
        return new Emitido(token, expiraEm);
    }

    public record Emitido(String token, Instant expiraEm) {
        @Override
        public String toString() {
            return "Emitido[token=***, expiraEm=" + expiraEm + "]";
        }
    }
}
