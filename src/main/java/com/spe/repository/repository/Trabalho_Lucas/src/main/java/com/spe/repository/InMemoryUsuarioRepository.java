package com.spe.repository;

import com.spe.model.Usuario;
import java.util.*;

public class InMemoryUsuarioRepository implements UsuarioRepository {
    private final Map<String, Usuario> dados = new HashMap<>();

    @Override public Usuario salvar(Usuario u) { dados.put(u.getId(), u); return u; }
    @Override public Optional<Usuario> buscar(String id) { return Optional.ofNullable(dados.get(id)); }
    @Override public List<Usuario> listar() { return new ArrayList<>(dados.values()); }

    @Override
    public Optional<Usuario> buscarPorLogin(String login) {
        return dados.values().stream().filter(u -> u.getLogin().equals(login)).findFirst();
    }
}
