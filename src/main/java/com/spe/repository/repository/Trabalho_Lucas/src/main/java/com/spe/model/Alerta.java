package com.spe.model;

import java.time.LocalDateTime;

public class Alerta {
    private final TipoAlerta tipo;
    private final String mensagem;
    private final LocalDateTime data;

    public Alerta(TipoAlerta tipo, String mensagem) {
        this.tipo = tipo;
        this.mensagem = mensagem;
        this.data = LocalDateTime.now();
    }

    public TipoAlerta getTipo() { return tipo; }
    public String getMensagem() { return mensagem; }
    public LocalDateTime getData() { return data; }

    @Override public String toString() {
        return "[ALERTA " + tipo + "] " + mensagem + " (" + data + ")";
    }
}
