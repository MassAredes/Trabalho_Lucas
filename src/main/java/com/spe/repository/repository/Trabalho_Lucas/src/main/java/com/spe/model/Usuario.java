package com.spe.model;

import java.util.UUID;

public class Usuario {
    private final String id;
    private final String login;
    private final String senhaHash;
    private final PapelUsuario papel;

    public Usuario(String login, String senhaHash, PapelUsuario papel) {
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
