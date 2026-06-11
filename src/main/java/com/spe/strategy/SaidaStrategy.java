package com.spe.strategy;

import com.spe.exception.DominioException;
import com.spe.model.Movimentacao;
import com.spe.model.Produto;
import java.time.LocalDate;

public class SaidaStrategy implements ValidacaoMovimentacaoStrategy {
    @Override public void validar(Produto p, Movimentacao m) {
        if (m.getQuantidade() > p.getQuantidade())
            throw new DominioException("estoque insuficiente");
        if (p.getValidade().isBefore(LocalDate.now()))
            throw new DominioException("produto vencido nao pode ser vendido — registre DESCARTE");
    }
    @Override public void aplicar(Produto p, Movimentacao m) { p.remover(m.getQuantidade()); }
}
