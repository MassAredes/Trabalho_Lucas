package com.spe.repository;

import com.spe.model.Movimentacao;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class InMemoryMovimentacaoRepository implements MovimentacaoRepository {
    // LinkedHashMap mantem a ordem de insercao, o que ajuda no historico.
    private final Map<String, Movimentacao> dados = new LinkedHashMap<>();

    @Override
    public Movimentacao salvar(Movimentacao m) {
        // Pega o id da movimentacao e usa esse valor como chave no mapa.
        dados.put(m.getId(), m);
        // Retorna a propria movimentacao salva, o que pode ser util para continuar o fluxo.
        return m;
    }

    @Override
    public Optional<Movimentacao> buscar(String id) {
        // Tenta achar a movimentacao pelo id informado.
        // Se nao encontrar nada, o Optional fica vazio em vez de dar erro.
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public List<Movimentacao> listar() {
        // Pega todos os valores do mapa e transforma em lista.
        // Assim fica mais facil percorrer o historico fora do repositorio.
        return new ArrayList<>(dados.values());
    }

    @Override
    public List<Movimentacao> listarPorProduto(String produtoId) {
        // Filtra so as movimentacoes do produto pedido.
        return dados.values().stream()
                .filter(m -> m.getProdutoId().equals(produtoId))
                .collect(Collectors.toList());
    }
}
