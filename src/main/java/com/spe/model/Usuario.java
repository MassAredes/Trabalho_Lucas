package com.spe.model;

import java.util.UUID;

public class Usuario {
    // Id interno do usuario.
    private final String id;
    // Login usado para entrar no sistema.
    private final String login;
    // A senha nao fica salva pura, e sim em formato hash.
    private final String senhaHash;
    // Papel define o nivel do usuario no sistema.
    private final PapelUsuario papel;

    public Usuario(String login, String senhaHash, PapelUsuario papel) {
        // O id automatico ajuda a diferenciar usuarios com seguranca.
        this.id = UUID.randomUUID().toString();
        this.login = login;
        this.senhaHash = senhaHash;
        this.papel = papel;
    }

    public String getId() { return id; }
    public String getLogin() { return login; }
    public String getSenhaHash() { return senhaHash; }
    public PapelUsuario getPapel() { return papel; }
}
