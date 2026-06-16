package com.spe.repository;

import com.spe.model.Movimentacao;

import java.util.List;

public interface MovimentacaoRepository extends Repository<Movimentacao> {
    // Filtro util para mostrar o historico de um produto so.
    List<Movimentacao> listarPorProduto(String produtoId);
}
