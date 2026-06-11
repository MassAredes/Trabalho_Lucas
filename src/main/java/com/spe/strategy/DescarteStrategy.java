package com.spe.strategy;

import com.spe.exception.DominioException;
import com.spe.model.Movimentacao;
import com.spe.model.Produto;

public class DescarteStrategy implements ValidacaoMovimentacaoStrategy {
    @Override public void validar(Produto p, Movimentacao m) {
        if (m.getQuantidade() > p.getQuantidade())
            throw new DominioException("nao e possivel descartar mais do que existe");
    }
    @Override public void aplicar(Produto p, Movimentacao m) { p.remover(m.getQuantidade()); }
}
