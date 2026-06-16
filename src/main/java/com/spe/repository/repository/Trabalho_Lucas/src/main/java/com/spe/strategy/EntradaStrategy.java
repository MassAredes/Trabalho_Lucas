package com.spe.strategy;

import com.spe.exception.DominioException;
import com.spe.model.Movimentacao;
import com.spe.model.Produto;

import java.time.LocalDate;

public class EntradaStrategy implements ValidacaoMovimentacaoStrategy {
    @Override
    public void validar(Produto p, Movimentacao m) {
        // Nao deixo entrar produto ja vencido, porque isso iria aumentar a perda.
        if (p.getValidade().isBefore(LocalDate.now()))
            throw new DominioException("nao e possivel dar entrada em produto vencido");
    }

    @Override
    public void aplicar(Produto p, Movimentacao m) {
        // Entrada sempre soma na quantidade atual.
        p.adicionar(m.getQuantidade());
    }
}
