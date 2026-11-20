package br.edu.unifor.missaomarte.servidor.seguranca;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.AuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

/**
 * Tudo exige token, exceto cadastro e login ("negar por padrão, liberar o público").
 * A API é sem estado: não há sessão nem cookie, então não há o que um site malicioso aproveite
 * com CSRF; o token vai no cabeçalho Authorization, que o navegador não envia sozinho.
 */
@Configuration
public class SegurancaConfig {
    private static final Logger LOG = LoggerFactory.getLogger(SegurancaConfig.class);
    static final int BYTES_MINIMOS_DO_SEGREDO = 32;

    @Bean
    SecurityFilterChain regras(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(regras -> regras
                        .requestMatchers(HttpMethod.POST, "/api/usuarios", "/api/auth/login").permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(o -> o.jwt(Customizer.withDefaults()).authenticationEntryPoint(SegurancaConfig::naoAutorizado))
                .exceptionHandling(e -> e.authenticationEntryPoint(SegurancaConfig::naoAutorizado))
                .build();
    }

    /** 401 também no formato ProblemDetail, sem dizer se o token faltou, expirou ou foi adulterado. */
    private static void naoAutorizado(HttpServletRequest requisicao, HttpServletResponse resposta,
                                      AuthenticationException erro) throws IOException {
        resposta.setStatus(HttpStatus.UNAUTHORIZED.value());
        resposta.setContentType("application/problem+json");
        resposta.setCharacterEncoding("UTF-8");
        resposta.getWriter().write("{\"title\":\"Unauthorized\",\"status\":401,"
                + "\"detail\":\"Token ausente, inválido ou expirado.\"}");
    }

    /** BCrypt com custo 10: lento de propósito e com sal aleatório embutido em cada hash. */
    @Bean
    PasswordEncoder codificadorDeSenha() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    SecretKey chaveDoToken(@Value("${missaomarte.jwt.segredo:}") String segredo) {
        byte[] bytes;
        if (segredo.isBlank()) {
            bytes = new byte[BYTES_MINIMOS_DO_SEGREDO];
            new SecureRandom().nextBytes(bytes);
            LOG.warn("MISSAOMARTE_JWT_SEGREDO não definida: usando segredo aleatório; os tokens valem só até reiniciar.");
        } else {
            bytes = segredo.getBytes(StandardCharsets.UTF_8);
            if (bytes.length < BYTES_MINIMOS_DO_SEGREDO) {
                throw new IllegalStateException("O segredo do token precisa de pelo menos "
                        + BYTES_MINIMOS_DO_SEGREDO + " bytes.");
            }
        }
        return new SecretKeySpec(bytes, "HmacSHA256");
    }

    @Bean
    JwtEncoder codificadorDeToken(SecretKey chave) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(chave));
    }

    /** Valida assinatura HS256 e expiração de cada token recebido (o resource server chama este bean). */
    @Bean
    JwtDecoder decodificadorDeToken(SecretKey chave) {
        return NimbusJwtDecoder.withSecretKey(chave).macAlgorithm(MacAlgorithm.HS256).build();
    }
}
