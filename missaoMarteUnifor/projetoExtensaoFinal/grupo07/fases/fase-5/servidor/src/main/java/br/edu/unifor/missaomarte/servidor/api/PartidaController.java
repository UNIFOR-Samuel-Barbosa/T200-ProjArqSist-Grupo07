package br.edu.unifor.missaomarte.servidor.api;

import br.edu.unifor.missaomarte.servidor.api.dto.NovaPartida;
import br.edu.unifor.missaomarte.servidor.api.dto.Respostas.LinhaDoRanking;
import br.edu.unifor.missaomarte.servidor.api.dto.Respostas.PartidaRegistrada;
import br.edu.unifor.missaomarte.servidor.persistencia.Partida;
import br.edu.unifor.missaomarte.servidor.servico.PartidaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.IntStream;

/** Rotas protegidas. O usuário é sempre o do token ({@code sub}), validado pelo filtro do resource server. */
@RestController
@Validated
public class PartidaController {
    private final PartidaService partidas;

    public PartidaController(PartidaService partidas) {
        this.partidas = partidas;
    }

    @PostMapping("/api/partidas")
    @ResponseStatus(HttpStatus.CREATED)
    public PartidaRegistrada registrar(@AuthenticationPrincipal Jwt token, @Valid @RequestBody NovaPartida corpo) {
        Partida partida = partidas.registrar(usuarioId(token), corpo.dificuldade(), corpo.resultado(),
                corpo.pontuacao(), corpo.duracaoSegundos(), corpo.movimentos());
        return PartidaRegistrada.de(partida);
    }

    @GetMapping("/api/partidas/minhas")
    public List<PartidaRegistrada> minhas(@AuthenticationPrincipal Jwt token,
                                          @RequestParam(defaultValue = "0") @Min(0) int pagina,
                                          @RequestParam(defaultValue = "10") @Min(1) @Max(50) int tamanho) {
        return partidas.historico(usuarioId(token), pagina, tamanho).stream().map(PartidaRegistrada::de).toList();
    }

    @GetMapping("/api/ranking")
    public List<LinhaDoRanking> ranking() {
        List<Partida> melhores = partidas.ranking();
        return IntStream.range(0, melhores.size()).mapToObj(i -> LinhaDoRanking.de(i + 1, melhores.get(i))).toList();
    }

    private static long usuarioId(Jwt token) {
        return Long.parseLong(token.getSubject());
    }
}
