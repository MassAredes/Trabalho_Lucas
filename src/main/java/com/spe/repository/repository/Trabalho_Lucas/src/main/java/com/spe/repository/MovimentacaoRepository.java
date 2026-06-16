package com.spe.repository;

import com.spe.model.Movimentacao;
import java.util.List;

public interface MovimentacaoRepository extends Repository<Movimentacao> {
    List<Movimentacao> listarPorProduto(String produtoId);
}
