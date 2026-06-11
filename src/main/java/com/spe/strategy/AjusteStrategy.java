package com.spe.strategy;

import com.spe.model.Movimentacao;
import com.spe.model.Produto;

public class AjusteStrategy implements ValidacaoMovimentacaoStrategy {
    @Override public void validar(Produto p, Movimentacao m) { /* permitido qualquer ajuste >=0 */ }
    @Override public void aplicar(Produto p, Movimentacao m) { p.ajustar(m.getQuantidade()); }
}
