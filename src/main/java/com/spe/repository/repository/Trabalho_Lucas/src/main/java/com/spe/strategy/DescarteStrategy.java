package com.spe.strategy;

import com.spe.exception.DominioException;
import com.spe.model.Movimentacao;
import com.spe.model.Produto;

public class DescarteStrategy implements ValidacaoMovimentacaoStrategy {
    @Override
    public void validar(Produto p, Movimentacao m) {
        // Nao posso descartar mais unidades do que existem no estoque.
        if (m.getQuantidade() > p.getQuantidade())
            throw new DominioException("nao e possivel descartar mais do que existe");
    }

    @Override
    public void aplicar(Produto p, Movimentacao m) {
        // O descarte tira itens do estoque, igual a uma remocao.
        p.remover(m.getQuantidade());
    }
}
