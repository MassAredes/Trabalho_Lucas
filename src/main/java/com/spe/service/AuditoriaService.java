package com.spe.service;

import com.spe.model.Movimentacao;
import com.spe.repository.MovimentacaoRepository;
import java.util.List;

/** SRP: apenas registra e consulta auditoria. */
public class AuditoriaService {
    private final MovimentacaoRepository repo;

    public AuditoriaService(MovimentacaoRepository repo) { this.repo = repo; }

    public void registrar(Movimentacao m) { repo.salvar(m); }

    public List<Movimentacao> historicoPorProduto(String produtoId) {
        return repo.listarPorProduto(produtoId);
    }

    public List<Movimentacao> historicoCompleto() { return repo.listar(); }
}
