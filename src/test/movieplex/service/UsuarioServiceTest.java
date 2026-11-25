
package movieplex.service;

import movieplex.model.Assinatura;
import movieplex.model.Usuario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioServiceTest {

    private UsuarioService usuarioService;
    private Usuario usuario;

    @BeforeEach
    void configurar() {
        usuarioService = new UsuarioService();

        usuario = new Usuario(
                "Gustavo",
                "gustavo@email.com",
                "123456"
        );
    }

    private Assinatura criarAssinaturaValida() {
        return new Assinatura(
                "Premium",
                39.90,
                LocalDate.now().minusDays(10),
                LocalDate.now().plusDays(30)
        );
    }

    // TESTES DE CADASTRO - R1

    @Test
    void deveCadastrarUsuario() {
        assertTrue(usuarioService.cadastrarUsuario(usuario));

        assertEquals(1, usuarioService.listarUsuarios().size());
    }

    @Test
    void naoDeveCadastrarUsuarioNulo() {
        assertFalse(usuarioService.cadastrarUsuario(null));
    }

    @Test
    void naoDeveCadastrarUsuarioComEmailNulo() {
        Usuario usuarioInvalido =
                new Usuario("Teste", null, "123456");

        assertFalse(usuarioService.cadastrarUsuario(usuarioInvalido));
    }

    @Test
    void naoDeveCadastrarUsuarioComEmailVazio() {
        Usuario usuarioInvalido =
                new Usuario("Teste", "   ", "123456");

        assertFalse(usuarioService.cadastrarUsuario(usuarioInvalido));
    }

    @Test
    void naoDeveCadastrarEmailDuplicado() {
        usuarioService.cadastrarUsuario(usuario);

        Usuario duplicado = new Usuario(
                "Outro",
                "GUSTAVO@EMAIL.COM",
                "654321"
        );

        assertFalse(usuarioService.cadastrarUsuario(duplicado));
    }

    // TESTES DE ALTERAÇÃO - R2

    @Test
    void deveAlterarCadastro() {
        usuarioService.cadastrarUsuario(usuario);

        assertTrue(
                usuarioService.alterarCadastro(
                        "gustavo@email.com",
                        "Gustavo Atualizado",
                        "novo@email.com",
                        "654321"
                )
        );

        Usuario atualizado =
                usuarioService.buscarUsuario("novo@email.com");

        assertNotNull(atualizado);
        assertEquals("Gustavo Atualizado", atualizado.getNome());
        assertEquals("654321", atualizado.getSenha());
    }

    @Test
    void naoDeveAlterarUsuarioInexistente() {
        assertFalse(
                usuarioService.alterarCadastro(
                        "inexistente@email.com",
                        "Novo Nome",
                        "novo@email.com",
                        "123456"
                )
        );
    }

    @Test
    void naoDeveAlterarParaEmailVazio() {
        usuarioService.cadastrarUsuario(usuario);

        assertFalse(
                usuarioService.alterarCadastro(
                        "gustavo@email.com",
                        "Novo Nome",
                        " ",
                        "123456"
                )
        );
    }

    @Test
    void naoDeveAlterarParaEmailDeOutroUsuario() {
        usuarioService.cadastrarUsuario(usuario);

        Usuario outro = new Usuario(
                "Maria",
                "maria@email.com",
                "123456"
        );

        usuarioService.cadastrarUsuario(outro);

        assertFalse(
                usuarioService.alterarCadastro(
                        "gustavo@email.com",
                        "Gustavo",
                        "maria@email.com",
                        "654321"
                )
        );
    }

    // TESTES DE EXCLUSÃO - R3

    @Test
    void deveExcluirUsuario() {
        usuarioService.cadastrarUsuario(usuario);

        assertTrue(
                usuarioService.excluirCadastro("gustavo@email.com")
        );

        assertNull(
                usuarioService.buscarUsuario("gustavo@email.com")
        );
    }

    @Test
    void naoDeveExcluirUsuarioInexistente() {
        assertFalse(
                usuarioService.excluirCadastro("inexistente@email.com")
        );
    }

    // TESTES DE LOGIN - R4

    @Test
    void deveRealizarLoginComCredenciaisCorretas() {
        usuarioService.cadastrarUsuario(usuario);

        Usuario resultado = usuarioService.realizarLogin(
                "gustavo@email.com",
                "123456"
        );

        assertNotNull(resultado);
        assertEquals(usuario, resultado);
    }

    @Test
    void naoDeveRealizarLoginComSenhaIncorreta() {
        usuarioService.cadastrarUsuario(usuario);

        assertNull(
                usuarioService.realizarLogin(
                        "gustavo@email.com",
                        "senhaerrada"
                )
        );
    }

    @Test
    void naoDeveRealizarLoginComUsuarioInexistente() {
        assertNull(
                usuarioService.realizarLogin(
                        "inexistente@email.com",
                        "123456"
                )
        );
    }

    // TESTES DE ASSINATURA - R5

    @Test
    void deveRealizarAssinatura() {
        usuarioService.cadastrarUsuario(usuario);

        Assinatura assinatura = criarAssinaturaValida();

        assertTrue(
                usuarioService.realizarAssinatura(
                        "gustavo@email.com",
                        assinatura
                )
        );

        assertEquals(1, usuario.getAssinaturas().size());
    }

    @Test
    void naoDeveAssinarComUsuarioInexistente() {
        assertFalse(
                usuarioService.realizarAssinatura(
                        "inexistente@email.com",
                        criarAssinaturaValida()
                )
        );
    }

    @Test
    void naoDeveAssinarComAssinaturaNula() {
        usuarioService.cadastrarUsuario(usuario);

        assertFalse(
                usuarioService.realizarAssinatura(
                        "gustavo@email.com",
                        null
                )
        );
    }

    @Test
    void naoDeveRealizarAssinaturaForaDaValidade() {
        usuarioService.cadastrarUsuario(usuario);

        Assinatura vencida = new Assinatura(
                "Premium",
                39.90,
                LocalDate.now().minusDays(30),
                LocalDate.now().minusDays(1)
        );

        assertFalse(
                usuarioService.realizarAssinatura(
                        "gustavo@email.com",
                        vencida
                )
        );
    }

    // TESTES DE ALTERAÇÃO DE ASSINATURA - R6

    @Test
    void deveAlterarAssinatura() {
        usuarioService.cadastrarUsuario(usuario);

        usuario.adicionarAssinatura(criarAssinaturaValida());

        assertTrue(
                usuarioService.alterarAssinatura(
                        "gustavo@email.com",
                        "Básico",
                        19.90
                )
        );

        Assinatura assinatura = usuario.getAssinaturas().get(0);

        assertEquals("Básico", assinatura.getPlano());
        assertEquals(19.90, assinatura.getValor());
    }

    @Test
    void naoDeveAlterarAssinaturaComUsuarioInexistente() {
        assertFalse(
                usuarioService.alterarAssinatura(
                        "inexistente@email.com",
                        "Básico",
                        19.90
                )
        );
    }

    @Test
    void naoDeveAlterarAssinaturaComPlanoVazio() {
        usuarioService.cadastrarUsuario(usuario);
        usuario.adicionarAssinatura(criarAssinaturaValida());

        assertFalse(
                usuarioService.alterarAssinatura(
                        "gustavo@email.com",
                        " ",
                        19.90
                )
        );
    }

    @Test
    void naoDeveAlterarAssinaturaComValorZero() {
        usuarioService.cadastrarUsuario(usuario);
        usuario.adicionarAssinatura(criarAssinaturaValida());

        assertFalse(
                usuarioService.alterarAssinatura(
                        "gustavo@email.com",
                        "Básico",
                        0
                )
        );
    }

    @Test
    void naoDeveAlterarAssinaturaSemAssinaturaAtiva() {
        usuarioService.cadastrarUsuario(usuario);

        assertFalse(
                usuarioService.alterarAssinatura(
                        "gustavo@email.com",
                        "Básico",
                        19.90
                )
        );
    }

    // TESTES DE CANCELAMENTO - R7

    @Test
    void deveCancelarAssinatura() {
        usuarioService.cadastrarUsuario(usuario);

        Assinatura assinatura = criarAssinaturaValida();
        usuario.adicionarAssinatura(assinatura);

        assertTrue(
                usuarioService.cancelarAssinatura(
                        "gustavo@email.com"
                )
        );

        assertFalse(assinatura.isAtivo());
    }

    @Test
    void naoDeveCancelarAssinaturaDeUsuarioInexistente() {
        assertFalse(
                usuarioService.cancelarAssinatura(
                        "inexistente@email.com"
                )
        );
    }

    @Test
    void naoDeveCancelarQuandoNaoExisteAssinatura() {
        usuarioService.cadastrarUsuario(usuario);

        assertFalse(
                usuarioService.cancelarAssinatura(
                        "gustavo@email.com"
                )
        );
    }

    // TESTES DE BUSCA E LISTAGEM

    @Test
    void deveBuscarUsuarioPorEmail() {
        usuarioService.cadastrarUsuario(usuario);

        Usuario resultado =
                usuarioService.buscarUsuario("GUSTAVO@EMAIL.COM");

        assertNotNull(resultado);
        assertEquals(usuario, resultado);
    }

    @Test
    void deveRetornarNuloAoBuscarEmailNulo() {
        assertNull(usuarioService.buscarUsuario(null));
    }

    @Test
    void deveListarUsuarios() {
        usuarioService.cadastrarUsuario(usuario);

        assertEquals(1, usuarioService.listarUsuarios().size());
    }

    @Test
    void deveRetornarListaVaziaSemUsuarios() {
        assertTrue(usuarioService.listarUsuarios().isEmpty());
    }
}
