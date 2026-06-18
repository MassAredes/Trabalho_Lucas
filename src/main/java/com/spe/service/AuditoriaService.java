package com.spe.service;

import com.spe.model.Movimentacao;
import com.spe.repository.MovimentacaoRepository;

import java.util.List;

// Esse servico cuida so do historico, sem misturar com a regra de estoque.
public class AuditoriaService {
    private final MovimentacaoRepository repo;

    public AuditoriaService(MovimentacaoRepository repo) { this.repo = repo; }

    public void registrar(Movimentacao m) {
        // Manda a movimentacao para o repositorio para ela entrar no historico.
        // E isso que permite consultar depois o que foi feito no estoque.
        repo.salvar(m);
    }

    public List<Movimentacao> historicoPorProduto(String produtoId) {
        // Retorna apenas o que aconteceu com um produto especifico.
        return repo.listarPorProduto(produtoId);
    }

    public List<Movimentacao> historicoCompleto() {
        // Traz todas as movimentacoes registradas, sem filtrar por produto.
        return repo.listar();
    }
}
