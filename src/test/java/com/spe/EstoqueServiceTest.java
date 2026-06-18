package com.spe;

import com.spe.exception.DominioException;
import com.spe.factory.MovimentacaoFactory;
import com.spe.model.Alerta;
import com.spe.model.Movimentacao;
import com.spe.model.Produto;
import com.spe.model.TipoAlerta;
import com.spe.model.TipoMovimentacao;
import com.spe.observer.AlertaObserver;
import com.spe.observer.AlertaService;
import com.spe.repository.InMemoryMovimentacaoRepository;
import com.spe.repository.InMemoryProdutoRepository;
import com.spe.repository.MovimentacaoRepository;
import com.spe.repository.ProdutoRepository;
import com.spe.service.AuditoriaService;
import com.spe.service.EstoqueService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EstoqueServiceTest {
    private EstoqueService estoque;
    private AuditoriaService auditoria;
    private AlertaService alertas;
    private List<Alerta> recebidos;
    private AlertaObserver observerTeste;
    private String nomeTesteAtual;
    private ProdutoRepository produtoRepo;
    private MovimentacaoRepository movRepo;

    @BeforeEach
    void setup(TestInfo testInfo) {
        nomeTesteAtual = testInfo.getDisplayName();
        System.out.println("\n==================================================");
        System.out.println("[INICIO] " + nomeTesteAtual);
        System.out.println("Montando ambiente do teste...");

        produtoRepo = new InMemoryProdutoRepository();
        movRepo = new InMemoryMovimentacaoRepository();
        auditoria = new AuditoriaService(movRepo);
        alertas = AlertaService.getInstance();
        recebidos = new ArrayList<>();

        observerTeste = alerta -> {
            recebidos.add(alerta);
            System.out.println("[ALERTA RECEBIDO] " + alerta);
        };

        alertas.registrar(observerTeste);
        estoque = new EstoqueService(produtoRepo, auditoria, alertas);

        System.out.println("Repositorio de produtos em memoria criado.");
        System.out.println("Estado inicial do repositorio de produtos: "
                + produtoRepo.listar().size() + " produto(s) -> " + produtoRepo.listar());
        System.out.println("Repositorio de movimentacoes em memoria criado.");
        System.out.println("Estado inicial do repositorio de movimentacoes: "
                + movRepo.listar().size() + " movimentacao(oes) -> " + movRepo.listar());
        System.out.println("Lista local de alertas recebidos no teste: "
                + recebidos.size() + " alerta(s) -> " + recebidos);
        System.out.println("Servico de auditoria pronto. Historico inicial: "
                + auditoria.historicoCompleto().size() + " registro(s) -> " + auditoria.historicoCompleto());
        System.out.println("Servico de estoque pronto para receber cadastros e movimentacoes.");
    }

    @AfterEach
    void finalizarTeste() {
        // Remove o observer deste teste para nao acumular alertas repetidos no proximo.
        alertas.remover(observerTeste);
        System.out.println("[FIM] " + nomeTesteAtual);
        System.out.println("==================================================");
    }

    private void imprimirTabelaProdutos(String titulo, List<Produto> produtos) {
        System.out.println(titulo);
        System.out.println("+----------------------+------------+------------+");
        System.out.printf("| %-20s | %-10s | %-10s |%n", "Nome", "Lote", "Validade");
        System.out.println("+----------------------+------------+------------+");

        for (Produto produto : produtos) {
            System.out.printf("| %-20s | %-10s | %-10s |%n",
                    produto.getNome(), produto.getLote(), produto.getValidade());
        }

        if (produtos.isEmpty()) {
            System.out.printf("| %-46s |%n", "Nenhum produto encontrado");
        }

        System.out.println("+----------------------+------------+------------+");
    }

    private void imprimirTabelaAlertas(String titulo, List<Alerta> alertasParaMostrar) {
        System.out.println(titulo);
        System.out.println("+----------------+----------------------+------------+----------------------------+");
        System.out.printf("| %-14s | %-20s | %-10s | %-26s |%n",
                "Tipo", "Produto", "Lote", "Data do alerta");
        System.out.println("+----------------+----------------------+------------+----------------------------+");

        for (Alerta alerta : alertasParaMostrar) {
            String mensagem = alerta.getMensagem();
            String produto = extrairEntre(mensagem, "Produto ", " (lote");
            if (produto.isEmpty()) {
                produto = extrairEntre(mensagem, "Produto ", " vence");
            }
            String lote = extrairEntre(mensagem, "(lote ", ")");
            if (lote.isEmpty()) {
                lote = "-";
            }

            System.out.printf("| %-14s | %-20s | %-10s | %-26s |%n",
                    alerta.getTipo(), produto, lote, alerta.getData());
        }

        if (alertasParaMostrar.isEmpty()) {
            System.out.printf("| %-76s |%n", "Nenhum alerta encontrado");
        }

        System.out.println("+----------------+----------------------+------------+----------------------------+");
    }

    private void imprimirTabelaMovimentacoes(String titulo, List<Movimentacao> movimentacoes) {
        System.out.println(titulo);
        System.out.println("+------------+----------------------+------------+------------+");
        System.out.printf("| %-10s | %-20s | %-10s | %-10s |%n",
                "Tipo", "ProdutoId", "Qtd", "Usuario");
        System.out.println("+------------+----------------------+------------+------------+");

        for (Movimentacao movimentacao : movimentacoes) {
            String produtoIdCurto = movimentacao.getProdutoId().length() > 20
                    ? movimentacao.getProdutoId().substring(0, 20)
                    : movimentacao.getProdutoId();
            System.out.printf("| %-10s | %-20s | %-10s | %-10s |%n",
                    movimentacao.getTipo(), produtoIdCurto, movimentacao.getQuantidade(), movimentacao.getUsuarioId());
        }

        if (movimentacoes.isEmpty()) {
            System.out.printf("| %-52s |%n", "Nenhuma movimentacao encontrada");
        }

        System.out.println("+------------+----------------------+------------+------------+");
    }

    private String extrairEntre(String texto, String inicio, String fim) {
        int posInicio = texto.indexOf(inicio);
        if (posInicio < 0) return "";
        posInicio += inicio.length();

        int posFim = texto.indexOf(fim, posInicio);
        if (posFim < 0) return texto.substring(posInicio).trim();

        return texto.substring(posInicio, posFim).trim();
    }

    @Test
    void entradaAumentaQuantidade() {
        Produto p = estoque.cadastrarProduto(
                new Produto("X", "L1", LocalDate.now().plusMonths(6), 0, 5));
        imprimirTabelaProdutos("Tabela do produto criado para o teste:", List.of(p));

        Movimentacao movimentacao = MovimentacaoFactory.criar(
                p.getId(), TipoMovimentacao.ENTRADA, 10, "u1", "");
        System.out.println("Resumo do cenario:");
        System.out.println("- Quantidade inicial do produto: 0");
        System.out.println("- Quantidade de entrada aplicada: 10");
        System.out.println("- Quantidade final esperada: 10");
        System.out.println();

        estoque.registrarMovimentacao(movimentacao);
        System.out.println("Resultado da operacao:");
        System.out.println("- Quantidade apos a entrada: " + p.getQuantidade());
        imprimirTabelaProdutos("Tabela dos produtos no repositorio depois da entrada:", produtoRepo.listar());
        imprimirTabelaMovimentacoes("Tabela das movimentacoes registradas no repositorio:", movRepo.listar());
        System.out.println("- Quantidade de registros na auditoria: "
                + auditoria.historicoPorProduto(p.getId()).size());
        imprimirTabelaMovimentacoes("Tabela do historico do produto apos a entrada:",
                auditoria.historicoPorProduto(p.getId()));

        assertEquals(10, p.getQuantidade());
        assertEquals(1, auditoria.historicoPorProduto(p.getId()).size());
    }

    @Test
    void saidaAcimaDoEstoqueFalha() {
        Produto p = estoque.cadastrarProduto(
                new Produto("X", "L1", LocalDate.now().plusMonths(6), 2, 0));
        imprimirTabelaProdutos("Tabela do produto criado para o teste:", List.of(p));
        System.out.println("Resumo do cenario:");
        System.out.println("- Quantidade atual em estoque: 2");
        System.out.println("- Quantidade de saida tentada: 5");
        System.out.println("- Resultado esperado: excecao de dominio por estoque insuficiente");
        System.out.println();

        DominioException ex = assertThrows(DominioException.class, () -> estoque.registrarMovimentacao(
                MovimentacaoFactory.criar(p.getId(), TipoMovimentacao.SAIDA, 5, "u1", "")));

        System.out.println("Resultado da operacao:");
        System.out.println("- Retorno obtido: excecao de dominio");
        System.out.println("- Mensagem da excecao: " + ex.getMessage());
        imprimirTabelaProdutos("Tabela final do produto depois da tentativa:", List.of(p));
        imprimirTabelaMovimentacoes("Tabela das movimentacoes registradas no repositorio:", movRepo.listar());
    }

    @Test
    void produtoVencidoDisparaAlerta() {
        Produto produto1 = estoque.cadastrarProduto(
                new Produto("Dipirona", "L2", LocalDate.now().minusDays(1), 5, 0));
        Produto produto2 = estoque.cadastrarProduto(
                new Produto("Leite Integral", "L8", LocalDate.now().minusDays(3), 8, 0));
        Produto produto3 = estoque.cadastrarProduto(
                new Produto("Arroz", "L10", LocalDate.now().plusMonths(3), 12, 0));

        System.out.println("Produtos criados para o teste:");
        System.out.println("Produtos no repositorio antes da verificacao: " + produtoRepo.listar());
        imprimirTabelaProdutos("Tabela dos produtos cadastrados no teste:", produtoRepo.listar());

        List<Produto> vencidos = produtoRepo.listar().stream()
                .filter(produto -> produto.getValidade().isBefore(LocalDate.now()))
                .collect(Collectors.toList());

        imprimirTabelaProdutos("Tabela dos produtos vencidos encontrados antes da verificacao:", vencidos);
        System.out.println("Resumo do cenario:");
        System.out.println("- Data atual usada no teste: " + LocalDate.now());
        System.out.println("- Quantidade de produtos cadastrados: " + produtoRepo.listar().size());
        System.out.println("- Quantidade de produtos vencidos esperados: " + vencidos.size());
        System.out.println();
        System.out.println("Executando verificacao de alertas...");

        estoque.verificarAlertas();

        List<Alerta> alertasVencidosRecebidos = recebidos.stream()
                .filter(alerta -> alerta.getTipo() == TipoAlerta.VENCIDO)
                .collect(Collectors.toList());

        System.out.println();
        System.out.println("Resultado da verificacao:");
        System.out.println("- Quantidade total de alertas recebidos neste teste: " + recebidos.size());
        System.out.println("- Quantidade de alertas de vencido recebidos: " + alertasVencidosRecebidos.size());
        imprimirTabelaAlertas("Tabela dos alertas recebidos neste teste:", alertasVencidosRecebidos);

        List<Alerta> historicoVencidos = alertas.getHistorico().stream()
                .filter(alerta -> alerta.getTipo() == TipoAlerta.VENCIDO)
                .collect(Collectors.toList());
        System.out.println("Historico acumulado de alertas do tipo VENCIDO no servico:");
        imprimirTabelaAlertas("Tabela do historico interno de alertas vencidos:", historicoVencidos);

        assertEquals(2, vencidos.size());
        assertEquals(2, alertasVencidosRecebidos.size());
        assertTrue(recebidos.stream().anyMatch(a -> a.getTipo() == TipoAlerta.VENCIDO));
    }

    @Test
    void estoqueAbaixoDoMinimoDisparaAlerta() {
        Produto produto = estoque.cadastrarProduto(
                new Produto("Z", "L3", LocalDate.now().plusYears(1), 2, 5));
        System.out.println("Resumo do cenario:");
        System.out.println("- Estoque atual do produto: 2");
        System.out.println("- Estoque minimo configurado: 5");
        System.out.println("- Resultado esperado: alerta de estoque baixo");
        System.out.println();
        imprimirTabelaProdutos("Tabela do produto analisado no teste:", List.of(produto));
        System.out.println("Executando verificacao de alertas...");

        estoque.verificarAlertas();

        List<Alerta> alertasEstoqueBaixo = recebidos.stream()
                .filter(alerta -> alerta.getTipo() == TipoAlerta.ESTOQUE_BAIXO)
                .collect(Collectors.toList());

        System.out.println();
        System.out.println("Resultado da verificacao:");
        System.out.println("- Quantidade total de alertas recebidos neste teste: " + recebidos.size());
        System.out.println("- Quantidade de alertas de estoque baixo recebidos: " + alertasEstoqueBaixo.size());
        imprimirTabelaAlertas("Tabela dos alertas de estoque baixo recebidos neste teste:", alertasEstoqueBaixo);

        assertTrue(recebidos.stream().anyMatch(a -> a.getTipo() == TipoAlerta.ESTOQUE_BAIXO));
    }

    @Test
    void naoPermiteEntradaEmProdutoVencido() {
        Produto p = estoque.cadastrarProduto(
                new Produto("W", "L4", LocalDate.now().minusDays(1), 0, 0));
        imprimirTabelaProdutos("Tabela do produto vencido criado para o teste:", List.of(p));
        System.out.println("Resumo do cenario:");
        System.out.println("- Produto esta vencido desde: " + p.getValidade());
        System.out.println("- Quantidade de entrada tentada: 5");
        System.out.println("- Resultado esperado: excecao de dominio");
        System.out.println();

        DominioException ex = assertThrows(DominioException.class, () -> estoque.registrarMovimentacao(
                MovimentacaoFactory.criar(p.getId(), TipoMovimentacao.ENTRADA, 5, "u1", "")));

        System.out.println("Resultado da operacao:");
        System.out.println("- Retorno obtido: excecao de dominio");
        System.out.println("- Mensagem da excecao: " + ex.getMessage());
        imprimirTabelaProdutos("Tabela final do produto depois da tentativa:", List.of(p));
        imprimirTabelaMovimentacoes("Tabela das movimentacoes registradas no repositorio:", movRepo.listar());
    }
}
