package com.spe;

import com.spe.exception.DominioException;
import com.spe.factory.MovimentacaoFactory;
import com.spe.model.Movimentacao;
import com.spe.model.TipoMovimentacao;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testa o funcionamento da MovimentacaoFactory.
 *
 * Esta classe verifica se as movimentações são criadas corretamente
 * e se as validações obrigatórias estão sendo respeitadas.
 *
 * Conceitos aplicados:
 * - Testes Unitários (JUnit 5)
 * - Factory Method
 * - Validação de regras de negócio
 */

class MovimentacaoFactoryTest {
     
    /**
     * Verifica se uma movimentação válida é criada corretamente.
     *
     * Cenário:
     * - Produto: p1
     * - Tipo: ENTRADA
     * - Quantidade: 5
     *
     * Resultado esperado:
     * - A movimentação deve ser criada sem erros.
     * - Os dados informados devem ser armazenados corretamente.
     */
    
    @Test
    void criaMovimentacaoValida() {

        /** Cria uma movimentação utilizando a Factory.  */
        Movimentacao m = MovimentacaoFactory.criar("p1", TipoMovimentacao.ENTRADA, 5, "u1", "ok");

        /** Verifica se o ID do produto foi armazenado corretamente. */
        assertEquals("p1", m.getProdutoId());

         /** Verifica se a quantidade foi armazenada corretamente.*/
        assertEquals(5, m.getQuantidade());
    }

    /**
     * Verifica se o sistema impede movimentações
     * com quantidade igual ou menor que zero.
     *
     * Regra de negócio:
     * Não existe movimentação com quantidade inválida.
     */
    
    @Test
    void rejeitaQuantidadeInvalida() {

        /** Espera que uma exceção seja lançada, pois a quantidade informada é inválida. */
        assertThrows(DominioException.class,
                () -> MovimentacaoFactory.criar("p1", TipoMovimentacao.SAIDA, 0, "u1", ""));
    }

    /**
     * Verifica se o sistema impede a criação
     * de movimentações sem um produto associado.
     *
     * Regra de negócio:
     * Toda movimentação deve estar vinculada
     * a um produto válido.
     */
    
    @Test
    void rejeitaProdutoVazio() {
        assertThrows(DominioException.class,
                () -> MovimentacaoFactory.criar("", TipoMovimentacao.ENTRADA, 1, "u1", ""));
    }
}
