package com.spe.repository;

import com.spe.model.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends Repository<Usuario> {
    // Login e usado na autenticacao, por isso existe essa busca separada.
    Optional<Usuario> buscarPorLogin(String login);
}
