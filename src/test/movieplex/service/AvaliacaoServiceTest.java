
package movieplex.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import movieplex.model.Avaliacao;
import movieplex.model.Filme;
import movieplex.model.Historico;
import movieplex.model.Usuario;

class AvaliacaoServiceTest {

    private AvaliacaoService avaliacaoService;
    private Usuario usuario;
    private Filme filme;

    @BeforeEach
    void configurar() {
        avaliacaoService = new AvaliacaoService();

        usuario = new Usuario(
                "Gustavo",
                "gustavo@email.com",
                "123456"
        );

        filme = new Filme(
                "Interestelar",
                "Uma viagem pelo espaço.",
                12,
                169
        );
    }

    // Método auxiliar para registrar que o usuário assistiu ao filme
    private void registrarFilmeAssistido() {
        Historico historico = new Historico(filme, 0, 100);
        historico.marcarComoAssistido();
        usuario.adicionarHistorico(historico);
    }

    // TESTES DE REGISTRO

    @Test
    void deveRegistrarAvaliacaoComDadosValidos() {
        registrarFilmeAssistido();

        boolean resultado = avaliacaoService.avaliar(
                1, 5, "Excelente filme!", usuario, filme
        );

        assertTrue(resultado);
        assertEquals(1, avaliacaoService.listarAvaliacoes().size());
    }

    @Test
    void naoDeveAvaliarComUsuarioNulo() {
        assertFalse(
                avaliacaoService.avaliar(
                        1, 5, "Ótimo!", null, filme
                )
        );
    }

    @Test
    void naoDeveAvaliarComProducaoNula() {
        assertFalse(
                avaliacaoService.avaliar(
                        1, 5, "Ótimo!", usuario, null
                )
        );
    }

    @Test
    void naoDeveAvaliarConteudoNaoAssistido() {
        boolean resultado = avaliacaoService.avaliar(
                1, 5, "Ótimo!", usuario, filme
        );

        assertFalse(resultado);
        assertTrue(avaliacaoService.listarAvaliacoes().isEmpty());
    }

    @Test
    void naoDeveRegistrarNotaMenorQueUm() {
        registrarFilmeAssistido();

        assertFalse(
                avaliacaoService.avaliar(
                        1, 0, "Ruim", usuario, filme
                )
        );
    }

    @Test
    void naoDeveRegistrarNotaMaiorQueCinco() {
        registrarFilmeAssistido();

        assertFalse(
                avaliacaoService.avaliar(
                        1, 6, "Excelente", usuario, filme
                )
        );
    }

    @Test
    void deveAceitarNotaUm() {
        registrarFilmeAssistido();

        assertTrue(
                avaliacaoService.avaliar(
                        1, 1, "Muito ruim", usuario, filme
                )
        );
    }

    @Test
    void deveAceitarNotaCinco() {
        registrarFilmeAssistido();

        assertTrue(
                avaliacaoService.avaliar(
                        1, 5, "Excelente", usuario, filme
                )
        );
    }

    @Test
    void naoDevePermitirAvaliacaoDuplicada() {
        registrarFilmeAssistido();

        assertTrue(
                avaliacaoService.avaliar(
                        1, 5, "Excelente", usuario, filme
                )
        );

        assertFalse(
                avaliacaoService.avaliar(
                        2, 4, "Outra avaliação", usuario, filme
                )
        );

        assertEquals(1, avaliacaoService.listarAvaliacoes().size());
    }

    // TESTES DE EDIÇÃO

    @Test
    void deveEditarAvaliacaoExistente() {
        registrarFilmeAssistido();

        avaliacaoService.avaliar(
                1, 3, "Bom", usuario, filme
        );

        boolean resultado = avaliacaoService.editarAvaliacao(
                1, 5, "Excelente!", usuario
        );

        assertTrue(resultado);

        Avaliacao avaliacao =
                avaliacaoService.buscarAvaliacao(usuario, filme);

        assertEquals(5, avaliacao.getNota());
        assertEquals("Excelente!", avaliacao.getComentario());
    }

    @Test
    void naoDeveEditarAvaliacaoComNotaInvalida() {
        registrarFilmeAssistido();

        avaliacaoService.avaliar(
                1, 3, "Bom", usuario, filme
        );

        assertFalse(
                avaliacaoService.editarAvaliacao(
                        1, 6, "Nota inválida", usuario
                )
        );
    }

    @Test
    void naoDeveEditarComUsuarioNulo() {
        assertFalse(
                avaliacaoService.editarAvaliacao(
                        1, 5, "Teste", null
                )
        );
    }

    @Test
    void naoDeveEditarAvaliacaoInexistente() {
        assertFalse(
                avaliacaoService.editarAvaliacao(
                        99, 5, "Teste", usuario
                )
        );
    }

    @Test
    void naoDevePermitirEditarAvaliacaoDeOutroUsuario() {
        registrarFilmeAssistido();

        avaliacaoService.avaliar(
                1, 3, "Bom", usuario, filme
        );

        Usuario outroUsuario = new Usuario(
                "Outro",
                "outro@email.com",
                "123456"
        );

        assertFalse(
                avaliacaoService.editarAvaliacao(
                        1, 5, "Alteração indevida", outroUsuario
                )
        );
    }

    // TESTES DE CONSULTA

    @Test
    void deveListarAvaliacoesRegistradas() {
        registrarFilmeAssistido();

        avaliacaoService.avaliar(
                1, 5, "Excelente", usuario, filme
        );

        assertEquals(1, avaliacaoService.listarAvaliacoes().size());
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremAvaliacoes() {
        assertTrue(avaliacaoService.listarAvaliacoes().isEmpty());
    }

    @Test
    void deveBuscarAvaliacaoPorUsuarioEProducao() {
        registrarFilmeAssistido();

        avaliacaoService.avaliar(
                1, 5, "Excelente", usuario, filme
        );

        Avaliacao resultado =
                avaliacaoService.buscarAvaliacao(usuario, filme);

        assertNotNull(resultado);
        assertEquals(5, resultado.getNota());
    }

    @Test
    void deveRetornarNuloQuandoAvaliacaoNaoExistir() {
        assertNull(
                avaliacaoService.buscarAvaliacao(usuario, filme)
        );
    }
}
