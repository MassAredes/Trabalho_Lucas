package com.spe;

import com.spe.exception.DominioException;
import com.spe.factory.MovimentacaoFactory;
import com.spe.model.Movimentacao;
import com.spe.model.TipoMovimentacao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MovimentacaoFactoryTest {
    private String nomeTesteAtual;

    @BeforeEach
    void iniciar(TestInfo testInfo) {
        nomeTesteAtual = testInfo.getDisplayName();
        System.out.println("\n==================================================");
        System.out.println("[INICIO] " + nomeTesteAtual);
    }

    @AfterEach
    void finalizar() {
        System.out.println("[FIM] " + nomeTesteAtual);
        System.out.println("==================================================");
    }

    private void imprimirTabelaEntrada(String produtoId, TipoMovimentacao tipo, int quantidade,
                                       String usuarioId, String observacao) {
        System.out.println("Tabela dos dados de entrada:");
        System.out.println("+----------------+----------------------+");
        System.out.printf("| %-14s | %-20s |%n", "Campo", "Valor");
        System.out.println("+----------------+----------------------+");
        System.out.printf("| %-14s | %-20s |%n", "produtoId", produtoId);
        System.out.printf("| %-14s | %-20s |%n", "tipo", tipo);
        System.out.printf("| %-14s | %-20s |%n", "quantidade", quantidade);
        System.out.printf("| %-14s | %-20s |%n", "usuarioId", usuarioId);
        System.out.printf("| %-14s | %-20s |%n", "observacao", observacao);
        System.out.println("+----------------+----------------------+");
    }

    private void imprimirTabelaMovimentacao(Movimentacao m) {
        System.out.println("Tabela da movimentacao criada:");
        System.out.println("+----------------+--------------------------------------+");
        System.out.printf("| %-14s | %-36s |%n", "Campo", "Valor");
        System.out.println("+----------------+--------------------------------------+");
        System.out.printf("| %-14s | %-36s |%n", "id", m.getId());
        System.out.printf("| %-14s | %-36s |%n", "produtoId", m.getProdutoId());
        System.out.printf("| %-14s | %-36s |%n", "tipo", m.getTipo());
        System.out.printf("| %-14s | %-36s |%n", "quantidade", m.getQuantidade());
        System.out.printf("| %-14s | %-36s |%n", "usuarioId", m.getUsuarioId());
        System.out.printf("| %-14s | %-36s |%n", "data", m.getData());
        System.out.printf("| %-14s | %-36s |%n", "observacao", m.getObservacao());
        System.out.println("+----------------+--------------------------------------+");
    }

    @Test
    void criaMovimentacaoValida() {
        System.out.println("Resumo do cenario:");
        System.out.println("- A factory deve criar uma movimentacao valida");
        System.out.println("- Nenhuma excecao e esperada");
        System.out.println();

        imprimirTabelaEntrada("p1", TipoMovimentacao.ENTRADA, 5, "u1", "ok");

        Movimentacao m = MovimentacaoFactory.criar("p1", TipoMovimentacao.ENTRADA, 5, "u1", "ok");

        System.out.println("Resultado da operacao:");
        imprimirTabelaMovimentacao(m);
        System.out.println("- produtoId retornado: " + m.getProdutoId());
        System.out.println("- quantidade retornada: " + m.getQuantidade());

        assertEquals("p1", m.getProdutoId());
        assertEquals(5, m.getQuantidade());
    }

    @Test
    void rejeitaQuantidadeInvalida() {
        System.out.println("Resumo do cenario:");
        System.out.println("- A factory deve bloquear quantidade zero");
        System.out.println("- Uma excecao de dominio e esperada");
        System.out.println();

        imprimirTabelaEntrada("p1", TipoMovimentacao.SAIDA, 0, "u1", "");

        DominioException ex = assertThrows(DominioException.class,
                () -> MovimentacaoFactory.criar("p1", TipoMovimentacao.SAIDA, 0, "u1", ""));

        System.out.println("Resultado da operacao:");
        System.out.println("- Retorno obtido: excecao de dominio");
        System.out.println("- Mensagem da excecao: " + ex.getMessage());
    }

    @Test
    void criaMovimentacaoSaidaValida() {
        System.out.println("Resumo do cenario:");
        System.out.println("- A factory deve criar uma movimentacao de saida valida");
        System.out.println("- Nenhuma excecao e esperada");
        System.out.println();

        imprimirTabelaEntrada("p2", TipoMovimentacao.SAIDA, 3, "u2", "venda");

        Movimentacao m = MovimentacaoFactory.criar("p2", TipoMovimentacao.SAIDA, 3, "u2", "venda");

        System.out.println("Resultado da operacao:");
        imprimirTabelaMovimentacao(m);
        System.out.println("- produtoId retornado: " + m.getProdutoId());
        System.out.println("- quantidade retornada: " + m.getQuantidade());

        assertEquals("p2", m.getProdutoId());
        assertEquals(3, m.getQuantidade());
    }

    @Test
    void criaMovimentacaoDescarteValida() {
        System.out.println("Resumo do cenario:");
        System.out.println("- A factory deve criar uma movimentacao de descarte valida");
        System.out.println("- Nenhuma excecao e esperada");
        System.out.println();

        imprimirTabelaEntrada("p3", TipoMovimentacao.DESCARTE, 2, "u3", "produto danificado");

        Movimentacao m = MovimentacaoFactory.criar("p3", TipoMovimentacao.DESCARTE, 2, "u3", "produto danificado");

        System.out.println("Resultado da operacao:");
        imprimirTabelaMovimentacao(m);
        System.out.println("- produtoId retornado: " + m.getProdutoId());
        System.out.println("- quantidade retornada: " + m.getQuantidade());

        assertEquals("p3", m.getProdutoId());
        assertEquals(2, m.getQuantidade());
    }

    @Test
    void criaMovimentacaoAjusteValidaComZero() {
        System.out.println("Resumo do cenario:");
        System.out.println("- A factory deve permitir ajuste com quantidade zero");
        System.out.println("- Nenhuma excecao e esperada");
        System.out.println();

        imprimirTabelaEntrada("p4", TipoMovimentacao.AJUSTE, 0, "u4", "contagem corrigida");

        Movimentacao m = MovimentacaoFactory.criar("p4", TipoMovimentacao.AJUSTE, 0, "u4", "contagem corrigida");

        System.out.println("Resultado da operacao:");
        imprimirTabelaMovimentacao(m);
        System.out.println("- produtoId retornado: " + m.getProdutoId());
        System.out.println("- quantidade retornada: " + m.getQuantidade());

        assertEquals("p4", m.getProdutoId());
        assertEquals(0, m.getQuantidade());
    }

    @Test
    void rejeitaUsuarioVazio() {
        System.out.println("Resumo do cenario:");
        System.out.println("- A factory deve bloquear movimentacao sem usuario");
        System.out.println("- Uma excecao de dominio e esperada");
        System.out.println();

        imprimirTabelaEntrada("p5", TipoMovimentacao.ENTRADA, 4, "", "reposicao");

        DominioException ex = assertThrows(DominioException.class,
                () -> MovimentacaoFactory.criar("p5", TipoMovimentacao.ENTRADA, 4, "", "reposicao"));

        System.out.println("Resultado da operacao:");
        System.out.println("- Retorno obtido: excecao de dominio");
        System.out.println("- Mensagem da excecao: " + ex.getMessage());
    }

    @Test
    void rejeitaTipoNulo() {
        System.out.println("Resumo do cenario:");
        System.out.println("- A factory deve bloquear movimentacao sem tipo definido");
        System.out.println("- Uma excecao de dominio e esperada");
        System.out.println();

        imprimirTabelaEntrada("p6", null, 1, "u6", "sem tipo");

        DominioException ex = assertThrows(DominioException.class,
                () -> MovimentacaoFactory.criar("p6", null, 1, "u6", "sem tipo"));

        System.out.println("Resultado da operacao:");
        System.out.println("- Retorno obtido: excecao de dominio");
        System.out.println("- Mensagem da excecao: " + ex.getMessage());
    }

    @Test
    void rejeitaProdutoVazio() {
        System.out.println("Resumo do cenario:");
        System.out.println("- A factory deve bloquear movimentacao sem produto");
        System.out.println("- Uma excecao de dominio e esperada");
        System.out.println();

        imprimirTabelaEntrada("", TipoMovimentacao.ENTRADA, 1, "u1", "");

        DominioException ex = assertThrows(DominioException.class,
                () -> MovimentacaoFactory.criar("", TipoMovimentacao.ENTRADA, 1, "u1", ""));

        System.out.println("Resultado da operacao:");
        System.out.println("- Retorno obtido: excecao de dominio");
        System.out.println("- Mensagem da excecao: " + ex.getMessage());
    }
}
