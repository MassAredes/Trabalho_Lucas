package com.spe.repository;

import com.spe.model.Produto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryProdutoRepository implements ProdutoRepository {
    // Map simples para simular um banco de dados na memoria.
    private final Map<String, Produto> dados = new HashMap<>();

    @Override
    public Produto salvar(Produto p) {
        // Salva o produto dentro do mapa usando o id dele como chave.
        // Desse jeito, depois fica facil buscar exatamente esse produto.
        dados.put(p.getId(), p);
        // Retorna o mesmo produto que acabou de ser salvo.
        return p;
    }

    @Override
    public Optional<Produto> buscar(String id) {
        // Procura o produto pelo id.
        // Optional.ofNullable evita erro caso o id nao exista no mapa.
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public List<Produto> listar() {
        // Junta todos os produtos salvos e devolve em forma de lista.
        // Isso ajuda quando o sistema precisa varrer o estoque inteiro.
        return new ArrayList<>(dados.values());
    }
}
