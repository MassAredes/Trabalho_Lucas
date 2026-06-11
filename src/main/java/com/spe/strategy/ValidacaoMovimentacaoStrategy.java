package com.spe.strategy;

import com.spe.model.Movimentacao;
import com.spe.model.Produto;

public interface ValidacaoMovimentacaoStrategy {
    void validar(Produto produto, Movimentacao mov);
    void aplicar(Produto produto, Movimentacao mov);
}
