package com.spe.strategy;

import com.spe.model.Movimentacao;
import com.spe.model.Produto;

public class AjusteStrategy implements ValidacaoMovimentacaoStrategy {
    @Override
    public void validar(Produto p, Movimentacao m) {
        // O ajuste ja foi validado na factory, entao aqui nao precisa repetir.
    }

    @Override
    public void aplicar(Produto p, Movimentacao m) {
        // Diferente da entrada/saida, no ajuste eu defino o valor final.
        p.ajustar(m.getQuantidade());
    }
}
