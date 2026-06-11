package com.spe;

import com.spe.exception.DominioException;
import com.spe.factory.MovimentacaoFactory;
import com.spe.model.Movimentacao;
import com.spe.model.TipoMovimentacao;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MovimentacaoFactoryTest {
    @Test
    void criaMovimentacaoValida() {
        Movimentacao m = MovimentacaoFactory.criar("p1", TipoMovimentacao.ENTRADA, 5, "u1", "ok");
        assertEquals("p1", m.getProdutoId());
        assertEquals(5, m.getQuantidade());
    }

    @Test
    void rejeitaQuantidadeInvalida() {
        assertThrows(DominioException.class,
                () -> MovimentacaoFactory.criar("p1", TipoMovimentacao.SAIDA, 0, "u1", ""));
    }

    @Test
    void rejeitaProdutoVazio() {
        assertThrows(DominioException.class,
                () -> MovimentacaoFactory.criar("", TipoMovimentacao.ENTRADA, 1, "u1", ""));
    }
}
