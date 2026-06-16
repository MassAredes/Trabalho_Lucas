package com.spe.repository;

import com.spe.model.Movimentacao;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryMovimentacaoRepository implements MovimentacaoRepository {
    private final Map<String, Movimentacao> dados = new LinkedHashMap<>();

    @Override public Movimentacao salvar(Movimentacao m) { dados.put(m.getId(), m); return m; }
    @Override public Optional<Movimentacao> buscar(String id) { return Optional.ofNullable(dados.get(id)); }
    @Override public List<Movimentacao> listar() { return new ArrayList<>(dados.values()); }

    @Override
    public List<Movimentacao> listarPorProduto(String produtoId) {
        return dados.values().stream()
                .filter(m -> m.getProdutoId().equals(produtoId))
                .collect(Collectors.toList());
    }
}
