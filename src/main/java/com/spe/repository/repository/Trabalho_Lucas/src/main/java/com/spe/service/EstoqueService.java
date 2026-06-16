package com.spe.service;

import com.spe.exception.DominioException;
import com.spe.model.*;
import com.spe.observer.AlertaService;
import com.spe.repository.ProdutoRepository;
import com.spe.strategy.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.EnumMap;
import java.util.Map;

/**
 * Núcleo do sistema. Aplica:
 *  - DIP: depende de abstrações (ProdutoRepository, AuditoriaService).
 *  - OCP: tipos de movimentação plugáveis via Strategy.
 *  - Observer: dispara alertas após cada movimentação.
 */
public class EstoqueService {
    private final ProdutoRepository produtos;
    private final AuditoriaService auditoria;
    private final AlertaService alertas;
    private final Map<TipoMovimentacao, ValidacaoMovimentacaoStrategy> estrategias;
    private int diasAvisoVencimento = 7;

    public EstoqueService(ProdutoRepository produtos, AuditoriaService auditoria, AlertaService alertas) {
        this.produtos = produtos;
        this.auditoria = auditoria;
        this.alertas = alertas;
        this.estrategias = new EnumMap<>(TipoMovimentacao.class);
        estrategias.put(TipoMovimentacao.ENTRADA, new EntradaStrategy());
        estrategias.put(TipoMovimentacao.SAIDA, new SaidaStrategy());
        estrategias.put(TipoMovimentacao.AJUSTE, new AjusteStrategy());
        estrategias.put(TipoMovimentacao.DESCARTE, new DescarteStrategy());
    }

    public void setDiasAvisoVencimento(int dias) { this.diasAvisoVencimento = dias; }

    public Produto cadastrarProduto(Produto p) { return produtos.salvar(p); }

    public Produto buscarProduto(String id) {
        return produtos.buscar(id).orElseThrow(() -> new DominioException("produto nao encontrado"));
    }

    public void registrarMovimentacao(Movimentacao m) {
        Produto p = buscarProduto(m.getProdutoId());
        ValidacaoMovimentacaoStrategy s = estrategias.get(m.getTipo());
        if (s == null) throw new DominioException("tipo de movimentacao nao suportado");
        s.validar(p, m);
        s.aplicar(p, m);
        auditoria.registrar(m);
        verificarAlertas(p);
    }

    /** Verifica alertas para todos os produtos. */
    public void verificarAlertas() {
        for (Produto p : produtos.listar()) verificarAlertas(p);
    }

    private void verificarAlertas(Produto p) {
        LocalDate hoje = LocalDate.now();
        if (p.getValidade().isBefore(hoje)) {
            alertas.disparar(new Alerta(TipoAlerta.VENCIDO,
                    "Produto " + p.getNome() + " (lote " + p.getLote() + ") vencido em " + p.getValidade()));
        } else {
            long dias = ChronoUnit.DAYS.between(hoje, p.getValidade());
            if (dias <= diasAvisoVencimento) {
                alertas.disparar(new Alerta(TipoAlerta.VENCIMENTO_PROXIMO,
                        "Produto " + p.getNome() + " vence em " + dias + " dia(s)"));
            }
        }
        if (p.getQuantidade() <= p.getEstoqueMinimo()) {
            alertas.disparar(new Alerta(TipoAlerta.ESTOQUE_BAIXO,
                    "Produto " + p.getNome() + " com estoque " + p.getQuantidade()
                            + " (minimo " + p.getEstoqueMinimo() + ") — possivel extravio"));
        }
    }
}
