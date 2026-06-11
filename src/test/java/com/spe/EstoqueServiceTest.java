package com.spe;

import com.spe.exception.DominioException;
import com.spe.factory.MovimentacaoFactory;
import com.spe.model.*;
import com.spe.observer.AlertaObserver;
import com.spe.observer.AlertaService;
import com.spe.repository.*;
import com.spe.service.AuditoriaService;
import com.spe.service.EstoqueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EstoqueServiceTest {
    private EstoqueService estoque;
    private AuditoriaService auditoria;
    private AlertaService alertas;
    private List<Alerta> recebidos;

    @BeforeEach
    void setup() {
        ProdutoRepository pr = new InMemoryProdutoRepository();
        MovimentacaoRepository mr = new InMemoryMovimentacaoRepository();
        auditoria = new AuditoriaService(mr);
        alertas = AlertaService.getInstance();
        recebidos = new ArrayList<>();
        AlertaObserver obs = recebidos::add;
        alertas.registrar(obs);
        estoque = new EstoqueService(pr, auditoria, alertas);
    }

    @Test
    void entradaAumentaQuantidade() {
        Produto p = estoque.cadastrarProduto(
                new Produto("X", "L1", LocalDate.now().plusMonths(6), 0, 5));
        estoque.registrarMovimentacao(MovimentacaoFactory.criar(
                p.getId(), TipoMovimentacao.ENTRADA, 10, "u1", ""));
        assertEquals(10, p.getQuantidade());
        assertEquals(1, auditoria.historicoPorProduto(p.getId()).size());
    }

    @Test
    void saidaAcimaDoEstoqueFalha() {
        Produto p = estoque.cadastrarProduto(
                new Produto("X", "L1", LocalDate.now().plusMonths(6), 2, 0));
        assertThrows(DominioException.class, () -> estoque.registrarMovimentacao(
                MovimentacaoFactory.criar(p.getId(), TipoMovimentacao.SAIDA, 5, "u1", "")));
    }

    @Test
    void produtoVencidoDisparaAlerta() {
        estoque.cadastrarProduto(
                new Produto("Y", "L2", LocalDate.now().minusDays(1), 5, 0));
        estoque.verificarAlertas();
        assertTrue(recebidos.stream().anyMatch(a -> a.getTipo() == TipoAlerta.VENCIDO));
    }

    @Test
    void estoqueAbaixoDoMinimoDisparaAlerta() {
        estoque.cadastrarProduto(
                new Produto("Z", "L3", LocalDate.now().plusYears(1), 2, 5));
        estoque.verificarAlertas();
        assertTrue(recebidos.stream().anyMatch(a -> a.getTipo() == TipoAlerta.ESTOQUE_BAIXO));
    }

    @Test
    void naoPermiteEntradaEmProdutoVencido() {
        Produto p = estoque.cadastrarProduto(
                new Produto("W", "L4", LocalDate.now().minusDays(1), 0, 0));
        assertThrows(DominioException.class, () -> estoque.registrarMovimentacao(
                MovimentacaoFactory.criar(p.getId(), TipoMovimentacao.ENTRADA, 5, "u1", "")));
    }
}
