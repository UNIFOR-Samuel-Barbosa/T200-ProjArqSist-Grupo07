package br.edu.unifor.missaomarte.servidor.servico;

public class NomeEmUsoException extends RuntimeException {
    public NomeEmUsoException() {
        super("Este nome já está em uso.");
    }
}
