package br.edu.unifor.missaomarte.servidor.api;

import br.edu.unifor.missaomarte.servidor.api.dto.Credenciais;
import br.edu.unifor.missaomarte.servidor.api.dto.Respostas.Token;
import br.edu.unifor.missaomarte.servidor.api.dto.Respostas.UsuarioCriado;
import br.edu.unifor.missaomarte.servidor.seguranca.TokenService;
import br.edu.unifor.missaomarte.servidor.servico.ContaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Rotas públicas: cadastro e login. O controller só traduz HTTP; as regras ficam em ContaService. */
@RestController
public class ContaController {
    private final ContaService contas;

    public ContaController(ContaService contas) {
        this.contas = contas;
    }

    @PostMapping("/api/usuarios")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioCriado cadastrar(@Valid @RequestBody Credenciais credenciais) {
        return UsuarioCriado.de(contas.cadastrar(credenciais.nome(), credenciais.senha()));
    }

    @PostMapping("/api/auth/login")
    public Token entrar(@Valid @RequestBody Credenciais credenciais) {
        TokenService.Emitido emitido = contas.entrar(credenciais.nome(), credenciais.senha());
        return new Token(emitido.token(), "Bearer", emitido.expiraEm());
    }
}
