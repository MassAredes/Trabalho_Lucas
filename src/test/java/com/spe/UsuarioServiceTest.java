package com.spe;

import com.spe.exception.DominioException;
import com.spe.model.PapelUsuario;
import com.spe.repository.InMemoryUsuarioRepository;
import com.spe.service.UsuarioService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Testa as funcionalidades do UsuarioService.
 *
 * Esta classe verifica se o cadastro e a autenticação de usuários
 * funcionam corretamente e se as regras de negócio relacionadas
 * aos usuários estão sendo respeitadas.
 *
 * Conceitos aplicados:
 * - Testes Unitários (JUnit 5)
 * - Regras de Negócio
 * - Validação de Dados
 * - Qualidade de Software
 */
class UsuarioServiceTest {
       /**
     * Verifica se um usuário pode ser cadastrado e autenticado.
     *
     * Cenário:
     * - Usuário "ana" é cadastrado.
     * - Login correto deve funcionar.
     * - Login com senha incorreta deve falhar.
     */
    @Test
    void cadastroEAutenticacaoOk() {

        //** Cria o serviço de usuários utilizando um repositório em memória. */
        UsuarioService s = new UsuarioService(new InMemoryUsuarioRepository());

        //** Cadastra um novo usuário do tipo gerente. */
        s.cadastrar("ana", "senha123", PapelUsuario.GERENTE);

        //** Verifica se a autenticação com a senha correta funciona. */
        assertTrue(s.autenticar("ana", "senha123").isPresent());

        //** Verifica se a autenticação com senha incorreta falha. */
        assertFalse(s.autenticar("ana", "errada").isPresent());
    }

     /**
     * Verifica se o sistema impede o cadastro
     * de dois usuários com o mesmo login.
     *
     * Regra de negócio:
     * Cada login deve ser único dentro do sistema.
     */
    
    @Test
    void naoPermiteLoginDuplicado() {
        //** Cria o serviço de usuários. */
        UsuarioService s = new UsuarioService(new InMemoryUsuarioRepository());

        //** Cadastra o primeiro usuário. */
        s.cadastrar("ana", "senha123", PapelUsuario.GERENTE);

        //** Tenta cadastrar outro usuário utilizando o mesmo login. */
        //** O sistema deve lançar uma exceção. */
        assertThrows(DominioException.class, () -> s.cadastrar("ana", "outra123", PapelUsuario.OPERADOR));
    }

    /**
     * Verifica se o sistema impede senhas muito curtas.
     *
     * Regra de negócio:
     * A senha deve possuir um tamanho mínimo
     * para garantir maior segurança.
     */
    
    @Test
    void senhaCurtaFalha() {
        UsuarioService s = new UsuarioService(new InMemoryUsuarioRepository());
        assertThrows(DominioException.class, () -> s.cadastrar("x", "12", PapelUsuario.OPERADOR));
    }
}
