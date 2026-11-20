package br.edu.unifor.missaomarte.application;

/**
 * Fato ocorrido em um turno. A sessão só registra o que a regra já decidiu;
 * a interface reage aos eventos (som, efeito) sem interpretar o texto da mensagem.
 */
public enum EventoTurno {
    MOVIMENTO,
    EMBARQUE,
    ENTREGA,
    COLISAO,
    VITORIA,
    DERROTA
}
