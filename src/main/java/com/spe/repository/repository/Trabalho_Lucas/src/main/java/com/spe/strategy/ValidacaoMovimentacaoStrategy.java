package com.spe.strategy;

import com.spe.model.Movimentacao;
import com.spe.model.Produto;

public interface ValidacaoMovimentacaoStrategy {
    // Aqui vao as regras para decidir se a movimentacao pode acontecer.
    void validar(Produto produto, Movimentacao mov);

    // Se passou na validacao, esse metodo realmente altera o estoque.
    void aplicar(Produto produto, Movimentacao mov);
}
