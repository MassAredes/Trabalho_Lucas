package com.spe.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Movimentacao {
    // Id proprio da movimentacao para conseguir rastrear no historico.
    private final String id;
    // Guarda qual produto foi afetado por essa movimentacao.
    private final String produtoId;
    // Tipo diz se foi entrada, saida, ajuste ou descarte.
    private final TipoMovimentacao tipo;
    // Quantidade movimentada.
    private final int quantidade;
    // Usuario responsavel pela acao.
    private final String usuarioId;
    // Momento exato em que o registro foi criado.
    private final LocalDateTime data;
    // Campo livre para observacoes como "reposicao" ou "venda".
    private final String observacao;

    public Movimentacao(String produtoId, TipoMovimentacao tipo, int quantidade,
                        String usuarioId, String observacao) {
        // Igual ao produto, aqui o id e automatico para nao repetir.
        this.id = UUID.randomUUID().toString();
        this.produtoId = produtoId;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.usuarioId = usuarioId;
        // Se vier nulo, eu troco por vazio para evitar problema depois no toString.
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
