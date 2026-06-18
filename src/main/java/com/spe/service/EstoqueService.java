package com.spe.service;

import com.spe.exception.DominioException;
import com.spe.model.Alerta;
import com.spe.model.Movimentacao;
import com.spe.model.Produto;
import com.spe.model.TipoAlerta;
import com.spe.model.TipoMovimentacao;
import com.spe.observer.AlertaService;
import com.spe.repository.ProdutoRepository;
import com.spe.strategy.AjusteStrategy;
import com.spe.strategy.DescarteStrategy;
import com.spe.strategy.EntradaStrategy;
import com.spe.strategy.SaidaStrategy;
import com.spe.strategy.ValidacaoMovimentacaoStrategy;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.EnumMap;
import java.util.Map;

// Essa classe e o coracao do sistema.
// Quase toda regra principal de estoque passa por aqui.
public class EstoqueService {
    // Repositorio onde os produtos ficam guardados.
    private final ProdutoRepository produtos;
    // Servico usado para salvar o historico das movimentacoes.
    private final AuditoriaService auditoria;
    // Servico responsavel por avisar quando algo importante acontece.
    private final AlertaService alertas;
    // Mapa que liga cada tipo de movimentacao a sua regra especifica.
    private final Map<TipoMovimentacao, ValidacaoMovimentacaoStrategy> estrategias;
    // Define com quantos dias de antecedencia o sistema comeca a avisar sobre vencimento.
    private int diasAvisoVencimento = 7;

    public EstoqueService(ProdutoRepository produtos, AuditoriaService auditoria, AlertaService alertas) {
        this.produtos = produtos;
        this.auditoria = auditoria;
        this.alertas = alertas;
        this.estrategias = new EnumMap<>(TipoMovimentacao.class);

        // Aqui eu cadastro qual estrategia sera usada para cada tipo.
        estrategias.put(TipoMovimentacao.ENTRADA, new EntradaStrategy());
        estrategias.put(TipoMovimentacao.SAIDA, new SaidaStrategy());
        estrategias.put(TipoMovimentacao.AJUSTE, new AjusteStrategy());
        estrategias.put(TipoMovimentacao.DESCARTE, new DescarteStrategy());
    }

    public void setDiasAvisoVencimento(int dias) { this.diasAvisoVencimento = dias; }

    public Produto cadastrarProduto(Produto p) {
        // Envia o produto para o repositorio para ele ficar cadastrado no sistema.
        // Depois devolve o mesmo produto ja salvo.
        return produtos.salvar(p);
    }

    public Produto buscarProduto(String id) {
        // Se nao encontrar, ja interrompe com erro de dominio.
        return produtos.buscar(id).orElseThrow(() -> new DominioException("produto nao encontrado"));
    }

    public void registrarMovimentacao(Movimentacao m) {
        // Primeiro pega o produto que sera alterado.
        Produto p = buscarProduto(m.getProdutoId());

        // Depois escolhe a regra certa de acordo com o tipo da movimentacao.
        ValidacaoMovimentacaoStrategy s = estrategias.get(m.getTipo());
        if (s == null) throw new DominioException("tipo de movimentacao nao suportado");

        // Valida antes para nao mexer no estoque de forma errada.
        s.validar(p, m);
        // Se estiver tudo certo, ai sim aplica a mudanca na quantidade.
        s.aplicar(p, m);
        // Salva no historico para auditoria.
        auditoria.registrar(m);
        // Por fim, confere se essa movimentacao gerou algum alerta.
        verificarAlertas(p);
    }

    // Verifica alertas para todos os produtos cadastrados.
    public void verificarAlertas() {
        // Passa por todos os produtos cadastrados e verifica um por um.
        for (Produto p : produtos.listar()) verificarAlertas(p);
    }

    private void verificarAlertas(Produto p) {
        LocalDate hoje = LocalDate.now();

        // Primeiro ve se o produto ja venceu.
        if (p.getValidade().isBefore(hoje)) {
            alertas.disparar(new Alerta(TipoAlerta.VENCIDO,
                    "Produto " + p.getNome() + " (lote " + p.getLote() + ") vencido em " + p.getValidade()));
        } else {
            // Se ainda nao venceu, calcula quantos dias faltam.
            long dias = ChronoUnit.DAYS.between(hoje, p.getValidade());

            // Se estiver perto de vencer, o sistema ja avisa.
            if (dias <= diasAvisoVencimento) {
                alertas.disparar(new Alerta(TipoAlerta.VENCIMENTO_PROXIMO,
                        "Produto " + p.getNome() + " vence em " + dias + " dia(s)"));
            }
        }

        // Essa parte alerta quando a quantidade chega no minimo ou abaixo dele.
        if (p.getQuantidade() <= p.getEstoqueMinimo()) {
            alertas.disparar(new Alerta(TipoAlerta.ESTOQUE_BAIXO,
                    "Produto " + p.getNome() + " com estoque " + p.getQuantidade()
                            + " (minimo " + p.getEstoqueMinimo() + ") - possivel extravio"));
        }
    }
}
