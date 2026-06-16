package com.spe.repository;

import com.spe.model.Usuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryUsuarioRepository implements UsuarioRepository {
    // Guarda os usuarios na memoria usando o id como chave.
    private final Map<String, Usuario> dados = new HashMap<>();

    @Override
    public Usuario salvar(Usuario u) {
        // Guarda o usuario no mapa usando o id dele como referencia.
        dados.put(u.getId(), u);
        // Retorna o usuario salvo para quem chamou poder continuar usando.
        return u;
    }

    @Override
    public Optional<Usuario> buscar(String id) {
        // Faz a busca pelo id do usuario.
        // Se nao existir, o Optional volta vazio.
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public List<Usuario> listar() {
        // Converte os usuarios guardados no mapa para uma lista comum.
        return new ArrayList<>(dados.values());
    }

    @Override
    public Optional<Usuario> buscarPorLogin(String login) {
        // Percorre os usuarios e devolve o primeiro login igual.
        return dados.values().stream().filter(u -> u.getLogin().equals(login)).findFirst();
    }
}
