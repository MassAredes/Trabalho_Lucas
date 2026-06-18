package com.spe;

import com.spe.exception.DominioException;
import com.spe.model.PapelUsuario;
import com.spe.model.Usuario;
import com.spe.repository.InMemoryUsuarioRepository;
import com.spe.repository.UsuarioRepository;
import com.spe.service.UsuarioService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UsuarioServiceTest {
    private String nomeTesteAtual;
    private UsuarioRepository usuarioRepo;
    private UsuarioService usuarioService;

    @BeforeEach
    void iniciar(TestInfo testInfo) {
        nomeTesteAtual = testInfo.getDisplayName();
        usuarioRepo = new InMemoryUsuarioRepository();
        usuarioService = new UsuarioService(usuarioRepo);
        System.out.println("\n==================================================");
        System.out.println("[INICIO] " + nomeTesteAtual);
        System.out.println("Repositorio de usuarios em memoria criado.");
        imprimirTabelaUsuarios("Tabela inicial de usuarios no repositorio:", usuarioRepo.listar());
        System.out.println("Servico de usuario pronto para cadastro e autenticacao.");
    }

    @AfterEach
    void finalizar() {
        System.out.println("[FIM] " + nomeTesteAtual);
        System.out.println("==================================================");
    }

    private void imprimirTabelaUsuarios(String titulo, List<Usuario> usuarios) {
        System.out.println(titulo);
        System.out.println("+----------------------+----------------------+------------+");
        System.out.printf("| %-20s | %-20s | %-10s |%n", "Id", "Login", "Papel");
        System.out.println("+----------------------+----------------------+------------+");

        for (Usuario usuario : usuarios) {
            String idCurto = usuario.getId().length() > 20 ? usuario.getId().substring(0, 20) : usuario.getId();
            System.out.printf("| %-20s | %-20s | %-10s |%n",
                    idCurto, usuario.getLogin(), usuario.getPapel());
        }

        if (usuarios.isEmpty()) {
            System.out.printf("| %-58s |%n", "Nenhum usuario encontrado");
        }

        System.out.println("+----------------------+----------------------+------------+");
    }

    @Test
    void cadastroEAutenticacaoOk() {
        System.out.println("Resumo do cenario:");
        System.out.println("- Usuarios a cadastrar: ana (GERENTE), joao (OPERADOR), maria (GERENTE)");
        System.out.println("- O teste deve mostrar pelo menos 3 usuarios cadastrados");
        System.out.println("- O teste tambem deve mostrar cargos diferentes na tabela");
        System.out.println("- Senha correta para teste: senha123");
        System.out.println("- Senha incorreta para teste: errada");
        System.out.println();

        usuarioService.cadastrar("ana", "senha123", PapelUsuario.GERENTE);
        usuarioService.cadastrar("joao", "senha456", PapelUsuario.OPERADOR);
        usuarioService.cadastrar("maria", "senha789", PapelUsuario.GERENTE);

        Optional<?> autenticacaoCorreta = usuarioService.autenticar("ana", "senha123");
        Optional<?> autenticacaoErrada = usuarioService.autenticar("ana", "errada");

        System.out.println("Resultado da operacao:");
        imprimirTabelaUsuarios("Tabela de usuarios depois do cadastro:", usuarioRepo.listar());
        System.out.println("- Quantidade total de usuarios cadastrados: " + usuarioRepo.listar().size());
        System.out.println("- Autenticacao com senha correta: " + autenticacaoCorreta.isPresent());
        System.out.println("- Autenticacao com senha errada: " + autenticacaoErrada.isPresent());

        assertTrue(usuarioRepo.listar().size() >= 3);
        assertTrue(autenticacaoCorreta.isPresent());
        assertFalse(autenticacaoErrada.isPresent());
    }

    @Test
    void naoPermiteLoginDuplicado() {
        System.out.println("Resumo do cenario:");
        System.out.println("- Primeiro cadastro: login=ana, papel=GERENTE");
        System.out.println("- Segundo cadastro tentado: login=ana, papel=OPERADOR");
        System.out.println("- Regra esperada: o sistema deve bloquear login repetido");
        System.out.println();

        usuarioService.cadastrar("ana", "senha123", PapelUsuario.GERENTE);
        imprimirTabelaUsuarios("Tabela de usuarios depois do primeiro cadastro:", usuarioRepo.listar());

        DominioException ex = assertThrows(DominioException.class,
                () -> usuarioService.cadastrar("ana", "outra123", PapelUsuario.OPERADOR));

        System.out.println("Resultado da operacao:");
        System.out.println("- Retorno obtido: excecao de dominio");
        System.out.println("- Mensagem da excecao: " + ex.getMessage());
        imprimirTabelaUsuarios("Tabela final de usuarios depois da tentativa bloqueada:", usuarioRepo.listar());
    }

    @Test
    void senhaCurtaFalha() {
        System.out.println("Resumo do cenario:");
        System.out.println("- Login informado: x");
        System.out.println("- Senha informada: 12");
        System.out.println("- Regra esperada: senha muito curta deve ser recusada");
        System.out.println();

        DominioException ex = assertThrows(DominioException.class,
                () -> usuarioService.cadastrar("x", "12", PapelUsuario.OPERADOR));

        System.out.println("Resultado da operacao:");
        System.out.println("- Retorno obtido: excecao de dominio");
        System.out.println("- Mensagem da excecao: " + ex.getMessage());
        imprimirTabelaUsuarios("Tabela final de usuarios depois da tentativa bloqueada:", usuarioRepo.listar());
    }
}
