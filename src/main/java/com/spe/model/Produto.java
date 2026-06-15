package com.spe.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Produto {
    // Cada produto ganha um id proprio para o sistema conseguir localizar depois.
    private final String id;
    // Nome exibido para identificar o item no estoque.
    private String nome;
    // Lote ajuda a saber de qual remessa esse produto veio.
    private String lote;
    // Validade usada para disparar alertas e evitar perdas.
    private LocalDate validade;
    // Quantidade atual disponivel no estoque.
    private int quantidade;
    // Quantidade minima aceitavel antes de avisar que o estoque esta baixo.
    private int estoqueMinimo;

    public Produto(String nome, String lote, LocalDate validade, int quantidade, int estoqueMinimo) {
        // UUID foi usado para nao precisar montar id manualmente.
        this.id = UUID.randomUUID().toString();
        this.nome = Objects.requireNonNull(nome);
        this.lote = Objects.requireNonNull(lote);
        this.validade = Objects.requireNonNull(validade);
        // Aqui eu garanto que o produto nao nasce com numero invalido.
        if (quantidade < 0) throw new IllegalArgumentException("quantidade negativa");
        if (estoqueMinimo < 0) throw new IllegalArgumentException("estoque minimo negativo");
        this.quantidade = quantidade;
        this.estoqueMinimo = estoqueMinimo;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getLote() { return lote; }
    public LocalDate getValidade() { return validade; }
    public int getQuantidade() { return quantidade; }
    public int getEstoqueMinimo() { return estoqueMinimo; }

    public void setNome(String nome) { this.nome = nome; }
    public void setLote(String lote) { this.lote = lote; }
    public void setValidade(LocalDate validade) { this.validade = validade; }
    public void setEstoqueMinimo(int estoqueMinimo) { this.estoqueMinimo = estoqueMinimo; }

    // Soma itens quando entra mercadoria nova no estoque.
    public void adicionar(int qtd) {
        if (qtd <= 0) throw new IllegalArgumentException("quantidade deve ser positiva");
        this.quantidade += qtd;
    }

    // Remove itens quando acontece saida ou descarte.
    public void remover(int qtd) {
        if (qtd <= 0) throw new IllegalArgumentException("quantidade deve ser positiva");
        // Essa checagem evita deixar o estoque negativo.
        if (qtd > this.quantidade) throw new IllegalArgumentException("estoque insuficiente");
        this.quantidade -= qtd;
    }

    // Ajuste serve para corrigir a quantidade real quando houver divergencia.
    public void ajustar(int novaQuantidade) {
        if (novaQuantidade < 0) throw new IllegalArgumentException("quantidade negativa");
        this.quantidade = novaQuantidade;
    }

    @Override public String toString() {
        return "Produto{" + nome + " lote=" + lote + " qtd=" + quantidade + " val=" + validade + "}";
    }
}
