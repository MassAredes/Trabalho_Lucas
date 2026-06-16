package com.spe.service;

import com.spe.exception.DominioException;
import com.spe.model.PapelUsuario;
import com.spe.model.Usuario;
import com.spe.repository.UsuarioRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;

public class UsuarioService {
    private final UsuarioRepository repo;

    public UsuarioService(UsuarioRepository repo) { this.repo = repo; }

    public Usuario cadastrar(String login, String senha, PapelUsuario papel) {
        if (login == null || login.isBlank()) throw new DominioException("login obrigatorio");
        if (senha == null || senha.length() < 4) throw new DominioException("senha muito curta");
        if (repo.buscarPorLogin(login).isPresent())
            throw new DominioException("login ja existe");
        return repo.salvar(new Usuario(login, hash(senha), papel));
    }

    public Optional<Usuario> autenticar(String login, String senha) {
        return repo.buscarPorLogin(login).filter(u -> u.getSenhaHash().equals(hash(senha)));
    }

    private String hash(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(s.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
