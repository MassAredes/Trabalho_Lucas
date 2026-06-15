package com.spe.exception;

// Excecao usada quando alguma regra do negocio e quebrada.
public class DominioException extends RuntimeException {
    public DominioException(String msg) { super(msg); }
}
