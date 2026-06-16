package com.spe.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Movimentacao {
    private final String id;
    private final String produtoId;
    private final TipoMovimentacao tipo;
    private final int quantidade;
    private final String usuarioId;
    private final LocalDateTime data;
    private final String observacao;

    public Movimentacao(String produtoId, TipoMovimentacao tipo, int quantidade,
                        String usuarioId, String observacao) {
        this.id = UUID.randomUUID().toString();
        this.produtoId = produtoId;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.usuarioId = usuarioId;
        this.observacao = observacao == null ? "" : observacao;
        this.data = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getProdutoId() { return produtoId; }
    public TipoMovimentacao getTipo() { return tipo; }
    public int getQuantidade() { return quantidade; }
    public String getUsuarioId() { return usuarioId; }
    public LocalDateTime getData() { return data; }
    public String getObservacao() { return observacao; }

    @Override public String toString() {
        return "[" + data + "] " + tipo + " qtd=" + quantidade + " produto=" + produtoId
                + " usuario=" + usuarioId + (observacao.isEmpty() ? "" : " obs=" + observacao);
    }
}
