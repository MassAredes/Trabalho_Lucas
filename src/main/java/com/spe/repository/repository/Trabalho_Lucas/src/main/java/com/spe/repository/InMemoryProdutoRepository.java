package com.spe.repository;

import com.spe.model.Produto;
import java.util.*;

public class InMemoryProdutoRepository implements ProdutoRepository {
    private final Map<String, Produto> dados = new HashMap<>();

    @Override public Produto salvar(Produto p) { dados.put(p.getId(), p); return p; }
    @Override public Optional<Produto> buscar(String id) { return Optional.ofNullable(dados.get(id)); }
    @Override public List<Produto> listar() { return new ArrayList<>(dados.values()); }
}
