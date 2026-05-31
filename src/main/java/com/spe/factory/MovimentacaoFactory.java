package com.spe.factory;

import com.spe.exception.DominioException;
import com.spe.model.Movimentacao;
import com.spe.model.TipoMovimentacao;

/** Factory Method para criar Movimentacao com validações básicas. */
public class MovimentacaoFactory {
    public static Movimentacao criar(String produtoId, TipoMovimentacao tipo, int quantidade,
                                     String usuarioId, String observacao) {
        if (produtoId == null || produtoId.isBlank())
            throw new DominioException("produtoId obrigatorio");
        if (usuarioId == null || usuarioId.isBlank())
            throw new DominioException("usuarioId obrigatorio");
        if (tipo == null) throw new DominioException("tipo obrigatorio");
        if (quantidade <= 0 && tipo != TipoMovimentacao.AJUSTE)
            throw new DominioException("quantidade deve ser positiva");
        if (tipo == TipoMovimentacao.AJUSTE && quantidade < 0)
            throw new DominioException("ajuste nao pode ser negativo");
        return new Movimentacao(produtoId, tipo, quantidade, usuarioId, observacao);
    }
}
