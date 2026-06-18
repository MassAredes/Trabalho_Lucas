package com.spe.app;

import java.time.LocalDate;

import com.spe.factory.MovimentacaoFactory;
import com.spe.model.PapelUsuario;
import com.spe.model.Produto;
import com.spe.model.TipoMovimentacao;
import com.spe.model.Usuario;
import com.spe.observer.AlertaService;
import com.spe.observer.ConsoleAlertaObserver;
import com.spe.repository.InMemoryMovimentacaoRepository;
import com.spe.repository.InMemoryProdutoRepository;
import com.spe.repository.InMemoryUsuarioRepository;
import com.spe.repository.MovimentacaoRepository;
import com.spe.repository.ProdutoRepository;
import com.spe.repository.UsuarioRepository;
import com.spe.service.AuditoriaService;
import com.spe.service.EstoqueService;
import com.spe.service.UsuarioService;

// Esse main serve como demonstracao completa do fluxo do sistema.
public class Main {
    public static void main(String[] args) {
        // Aqui eu monto manualmente as dependencias para a aplicacao funcionar.
        ProdutoRepository produtoRepo = new InMemoryProdutoRepository();
        MovimentacaoRepository movRepo = new InMemoryMovimentacaoRepository();
        UsuarioRepository usuarioRepo = new InMemoryUsuarioRepository();

        // Registra um observer para os alertas aparecerem no console.
        AlertaService alertas = AlertaService.getInstance();
        alertas.registrar(new ConsoleAlertaObserver());

        // Servicos principais usados ao longo da execucao.
        AuditoriaService auditoria = new AuditoriaService(movRepo);
        UsuarioService usuarios = new UsuarioService(usuarioRepo);
        EstoqueService estoque = new EstoqueService(produtoRepo, auditoria, alertas);

        // 1. Cadastra dois usuarios so para simular uso real.
        Usuario gerente = usuarios.cadastrar("ana", "1234", PapelUsuario.GERENTE);
        Usuario operador = usuarios.cadastrar("joao", "1234", PapelUsuario.OPERADOR);
        System.out.println("Usuarios cadastrados: " + gerente.getLogin() + ", " + operador.getLogin());

        // 2. Cadastra produtos com dados basicos de lote, validade e estoque minimo.
        Produto dipirona = estoque.cadastrarProduto(
                new Produto("Dipirona 500mg", "L001", LocalDate.now().plusDays(3), 50, 10));
        Produto leite = estoque.cadastrarProduto(
                new Produto("Leite UHT", "L002", LocalDate.now().plusMonths(2), 5, 10));
        System.out.println("Produtos cadastrados: " + dipirona + " | " + leite);

        // 3. Registra uma saida e uma entrada para mostrar o estoque mudando.
        estoque.registrarMovimentacao(MovimentacaoFactory.criar(
                dipirona.getId(), TipoMovimentacao.SAIDA, 5, operador.getId(), "venda balcao"));
        estoque.registrarMovimentacao(MovimentacaoFactory.criar(
                leite.getId(), TipoMovimentacao.ENTRADA, 20, operador.getId(), "reposicao"));

        // 4. Faz uma rodada geral de verificacao para ver se ha avisos a mostrar.
        System.out.println("\n--- Verificando alertas ---");
        estoque.verificarAlertas();

        // 5. Mostra o historico de um produto para provar que a auditoria salvou as movimentacoes.
        System.out.println("\n--- Auditoria do produto Dipirona ---");
        auditoria.historicoPorProduto(dipirona.getId()).forEach(System.out::println);
    }
}
