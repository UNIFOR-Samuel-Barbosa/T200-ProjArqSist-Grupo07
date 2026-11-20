package br.edu.unifor.missaomarte.servidor.seguranca;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/** Relógio injetável: datas de cadastro, partidas e tokens vêm daqui (testes podem trocá-lo). */
@Configuration
public class RelogioConfig {
    @Bean
    Clock relogio() {
        return Clock.systemUTC();
    }
}
