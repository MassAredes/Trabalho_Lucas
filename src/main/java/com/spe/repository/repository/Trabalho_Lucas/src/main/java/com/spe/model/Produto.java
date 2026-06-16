package com.spe.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Produto {
    private final String id;
    private String nome;
    private String lote;
    private LocalDate validade;
    private int quantidade;
    private int estoqueMinimo;

    public Produto(String nome, String lote, LocalDate validade, int quantidade, int estoqueMinimo) {
        this.id = UUID.randomUUID().toString();
        this.nome = Objects.requireNonNull(nome);
        this.lote = Objects.requireNonNull(lote);
        this.validade = Objects.requireNonNull(validade);
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

    public void adicionar(int qtd) {
        if (qtd <= 0) throw new IllegalArgumentException("quantidade deve ser positiva");
        this.quantidade += qtd;
    }

    public void remover(int qtd) {
        if (qtd <= 0) throw new IllegalArgumentException("quantidade deve ser positiva");
        if (qtd > this.quantidade) throw new IllegalArgumentException("estoque insuficiente");
        this.quantidade -= qtd;
    }

    public void ajustar(int novaQuantidade) {
        if (novaQuantidade < 0) throw new IllegalArgumentException("quantidade negativa");
        this.quantidade = novaQuantidade;
    }

    @Override public String toString() {
        return "Produto{" + nome + " lote=" + lote + " qtd=" + quantidade + " val=" + validade + "}";
    }
}
