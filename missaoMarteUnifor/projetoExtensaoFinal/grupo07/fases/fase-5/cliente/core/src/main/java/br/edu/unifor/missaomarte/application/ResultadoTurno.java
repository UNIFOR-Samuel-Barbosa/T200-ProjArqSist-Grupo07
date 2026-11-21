package br.edu.unifor.missaomarte.application;

import java.util.List;

public record ResultadoTurno(boolean consumiuTurno, String mensagem, EstadoPartida estado,
                             List<EventoTurno> eventos) {
    public ResultadoTurno {
        eventos = List.copyOf(eventos);
    }

    /** Construtor da Fase 3, mantido para a mudança ser aditiva: turno sem eventos. */
    public ResultadoTurno(boolean consumiuTurno, String mensagem, EstadoPartida estado) {
        this(consumiuTurno, mensagem, estado, List.of());
    }

    public boolean teve(EventoTurno evento) {
        return eventos.contains(evento);
    }
}
