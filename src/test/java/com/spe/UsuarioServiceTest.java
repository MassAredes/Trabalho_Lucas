package com.spe;

import com.spe.exception.DominioException;
import com.spe.model.PapelUsuario;
import com.spe.repository.InMemoryUsuarioRepository;
import com.spe.service.UsuarioService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioServiceTest {
    @Test
    void cadastroEAutenticacaoOk() {
        UsuarioService s = new UsuarioService(new InMemoryUsuarioRepository());
        s.cadastrar("ana", "senha123", PapelUsuario.GERENTE);
        assertTrue(s.autenticar("ana", "senha123").isPresent());
        assertFalse(s.autenticar("ana", "errada").isPresent());
    }

    @Test
    void naoPermiteLoginDuplicado() {
        UsuarioService s = new UsuarioService(new InMemoryUsuarioRepository());
        s.cadastrar("ana", "senha123", PapelUsuario.GERENTE);
        assertThrows(DominioException.class, () -> s.cadastrar("ana", "outra123", PapelUsuario.OPERADOR));
    }

    @Test
    void senhaCurtaFalha() {
        UsuarioService s = new UsuarioService(new InMemoryUsuarioRepository());
        assertThrows(DominioException.class, () -> s.cadastrar("x", "12", PapelUsuario.OPERADOR));
    }
}
