package com.spe.factory;

import com.spe.exception.DominioException;
import com.spe.model.Movimentacao;
import com.spe.model.TipoMovimentacao;

// Essa factory centraliza a criacao de movimentacoes.
// A vantagem e que todo mundo cria do mesmo jeito e com as mesmas validacoes.
public class MovimentacaoFactory {
    public static Movimentacao criar(String produtoId, TipoMovimentacao tipo, int quantidade,
                                     String usuarioId, String observacao) {
        // Sem produto ou usuario nao faz sentido registrar a movimentacao.
        if (produtoId == null || produtoId.isBlank())
            throw new DominioException("produtoId obrigatorio");
        if (usuarioId == null || usuarioId.isBlank())
            throw new DominioException("usuarioId obrigatorio");
        if (tipo == null) throw new DominioException("tipo obrigatorio");

        // Para quase todos os casos a quantidade precisa ser positiva.
        if (quantidade <= 0 && tipo != TipoMovimentacao.AJUSTE)
            throw new DominioException("quantidade deve ser positiva");

        // No ajuste eu estou definindo o valor final do estoque, entao zero pode existir.
        if (tipo == TipoMovimentacao.AJUSTE && quantidade < 0)
            throw new DominioException("ajuste nao pode ser negativo");

        return new Movimentacao(produtoId, tipo, quantidade, usuarioId, observacao);
    }
}
