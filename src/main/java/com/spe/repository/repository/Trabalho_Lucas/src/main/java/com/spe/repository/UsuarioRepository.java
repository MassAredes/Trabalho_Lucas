package com.spe.repository;

import com.spe.model.Usuario;
import java.util.Optional;

public interface UsuarioRepository extends Repository<Usuario> {
    Optional<Usuario> buscarPorLogin(String login);
}
