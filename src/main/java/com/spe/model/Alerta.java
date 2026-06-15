package com.spe.model;

import java.time.LocalDateTime;

public class Alerta {
    // Tipo do alerta para saber o motivo do aviso.
    private final TipoAlerta tipo;
    // Mensagem que vai aparecer para o usuario.
    private final String mensagem;
    // Data e hora em que o alerta foi gerado.
    private final LocalDateTime data;

    public Alerta(TipoAlerta tipo, String mensagem) {
        this.tipo = tipo;
        this.mensagem = mensagem;
        // Guardo o momento do alerta para historico e auditoria.
        this.data = LocalDateTime.now();
    }

    public TipoAlerta getTipo() { return tipo; }
    public String getMensagem() { return mensagem; }
    public LocalDateTime getData() { return data; }

    @Override public String toString() {
        return "[ALERTA " + tipo + "] " + mensagem + " (" + data + ")";
    }
}
