package br.edu.unifor.missaomarte.servidor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Servidor do Missão Marte: cadastro, login, histórico de partidas e ranking global.
 * Organizado em camadas: {@code api} (controllers e DTOs) → {@code servico} (regras) →
 * {@code persistencia} (entidades e repositórios); {@code seguranca} configura hash e token.
 * O servidor não executa a lógica do jogo: guarda o resultado que o cliente envia.
 */
@SpringBootApplication
public class ServidorApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServidorApplication.class, args);
    }
}
