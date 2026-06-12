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
// Serviço principal responsável pelas regras de negócio do estoque.
// É a classe que estamos testando.
    private EstoqueService estoque;
    
// Responsável por registrar o histórico de todas as movimentações.
// Relacionado ao conceito de auditoria e rastreabilidade.
    
    private AuditoriaService auditoria;
    
// Serviço responsável por gerar e distribuir alertas.
// Utiliza os padrões Singleton e Observer.
    private AlertaService alertas;
    
// Lista utilizada para armazenar os alertas recebidos durante os testes.
// Permite verificar se os alertas foram realmente disparados.
    private List<Alerta> recebidos;

    @BeforeEach
    void setup() {
    // Cria um repositório em memória para armazenar produtos.
    // Simula um banco de dados sem necessidade de instalação.
        ProdutoRepository pr = new InMemoryProdutoRepository();
        
    // Cria um repositório em memória para armazenar movimentações.
        MovimentacaoRepository mr = new InMemoryMovimentacaoRepository();
        
    // Serviço responsável por registrar o histórico das operações.
        auditoria = new AuditoriaService(mr);
        
    // Obtém a única instância do serviço de alertas (Singleton).
        alertas = AlertaService.getInstance();

    // Inicializa a lista que armazenará os alertas gerados.
        recebidos = new ArrayList<>();

    // Cria um observador que adiciona cada alerta recebido na lista.
    // Demonstra a aplicação do padrão Observer.
        AlertaObserver obs = recebidos::add;

    // Registra o observador no serviço de alertas.
        alertas.registrar(obs);

    // Cria o serviço principal de estoque.
    // Recebe as dependências por injeção, aplicando DIP (SOLID).
        estoque = new EstoqueService(pr, auditoria, alertas);
    }

    @Test
    void entradaAumentaQuantidade() {
    // Cadastra um produto com estoque inicial igual a zero.
        Produto p = estoque.cadastrarProduto(
                new Produto("X", "L1", LocalDate.now().plusMonths(6), 0, 5));

    // Registra uma entrada de 10 unidades no estoque.
        estoque.registrarMovimentacao(MovimentacaoFactory.criar(
                p.getId(), TipoMovimentacao.ENTRADA, 10, "u1", ""));

    // Verifica se a quantidade foi atualizada corretamente.
        assertEquals(10, p.getQuantidade());

    // Verifica se a movimentação foi registrada na auditoria.
        assertEquals(1, auditoria.historicoPorProduto(p.getId()).size());
    }

    @Test
    void saidaAcimaDoEstoqueFalha() {

    // Cadastra um produto com apenas 2 unidades disponíveis.
        Produto p = estoque.cadastrarProduto(
                new Produto("X", "L1", LocalDate.now().plusMonths(6), 2, 0));

    // Tenta retirar 5 unidades.
    // O sistema deve impedir essa operação lançando uma exceção.
        assertThrows(DominioException.class, () -> estoque.registrarMovimentacao(
                MovimentacaoFactory.criar(p.getId(), TipoMovimentacao.SAIDA, 5, "u1", "")));
    }

    @Test
    void produtoVencidoDisparaAlerta() {
    // Cria um produto que venceu ontem.
        estoque.cadastrarProduto(
                new Produto("Y", "L2", LocalDate.now().minusDays(1), 5, 0));

    // Executa a verificação automática de alertas.
        estoque.verificarAlertas();

    // Confirma que foi gerado um alerta de produto vencido.
        assertTrue(recebidos.stream().anyMatch(a -> a.getTipo() == TipoAlerta.VENCIDO));
    }

    @Test
    void estoqueAbaixoDoMinimoDisparaAlerta() {
    // Produto possui apenas 2 unidades,
    // mas o estoque mínimo definido é 5.
        estoque.cadastrarProduto(
                new Produto("Z", "L3", LocalDate.now().plusYears(1), 2, 5));

    // Executa a verificação de alertas.
        estoque.verificarAlertas();

    // Verifica se foi gerado alerta de estoque baixo.
        assertTrue(recebidos.stream().anyMatch(a -> a.getTipo() == TipoAlerta.ESTOQUE_BAIXO));
    }

    @Test
    void naoPermiteEntradaEmProdutoVencido() {

    // Cria um produto já vencido.
        Produto p = estoque.cadastrarProduto(
                new Produto("W", "L4", LocalDate.now().minusDays(1), 0, 0));

    // O sistema não permite entrada de mercadorias vencidas.
    // Espera-se uma exceção de regra de negócio.
        assertThrows(DominioException.class, () -> estoque.registrarMovimentacao(
                MovimentacaoFactory.criar(p.getId(), TipoMovimentacao.ENTRADA, 5, "u1", "")));
    }
}
