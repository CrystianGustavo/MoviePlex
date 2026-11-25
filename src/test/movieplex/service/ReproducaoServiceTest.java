
package movieplex.service;

import movieplex.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReproducaoServiceTest {

    private ReproducaoService reproducaoService;
    private Usuario usuario;
    private Filme filme;

    @BeforeEach
    void configurar() {
        reproducaoService = new ReproducaoService();

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

        Assinatura assinatura = new Assinatura(
                "Premium",
                39.90,
                LocalDate.now().minusDays(10),
                LocalDate.now().plusDays(30)
        );

        usuario.adicionarAssinatura(assinatura);
    }

    // TESTES DE VOLUME

    @Test
    void deveIniciarComVolumeCinquenta() {
        assertEquals(50, reproducaoService.getVolume());
    }

    @Test
    void deveAlterarVolumeParaValorValido() {
        assertTrue(reproducaoService.ajustarVolume(80));
        assertEquals(80, reproducaoService.getVolume());
    }

    @Test
    void naoDeveAceitarVolumeNegativo() {
        assertFalse(reproducaoService.ajustarVolume(-1));
        assertEquals(50, reproducaoService.getVolume());
    }

    @Test
    void naoDeveAceitarVolumeAcimaDeCem() {
        assertFalse(reproducaoService.ajustarVolume(101));
        assertEquals(50, reproducaoService.getVolume());
    }

    @Test
    void deveAceitarVolumeZero() {
        assertTrue(reproducaoService.ajustarVolume(0));
        assertEquals(0, reproducaoService.getVolume());
    }

    @Test
    void deveAceitarVolumeCem() {
        assertTrue(reproducaoService.ajustarVolume(100));
        assertEquals(100, reproducaoService.getVolume());
    }

    // TESTES DE REPRODUÇÃO

    @Test
    void deveIniciarReproducaoComAssinaturaAtiva() {
        Historico historico =
                reproducaoService.assistir(usuario, filme);

        assertNotNull(historico);
        assertTrue(reproducaoService.estaReproduzindo(historico));
        assertTrue(usuario.getHistoricos().contains(historico));
    }

    @Test
    void naoDeveReproduzirComUsuarioNulo() {
        assertNull(reproducaoService.assistir(null, filme));
    }

    @Test
    void naoDeveReproduzirComProducaoNula() {
        assertNull(reproducaoService.assistir(usuario, null));
    }

    @Test
    void naoDeveReproduzirSemAssinaturaAtiva() {
        usuario.getAssinaturas().clear();

        assertNull(reproducaoService.assistir(usuario, filme));
    }

    @Test
    void naoDeveReproduzirComAssinaturaCancelada() {
        usuario.getAssinaturas().get(0).cancelarAssinatura();

        assertNull(reproducaoService.assistir(usuario, filme));
    }

    // TESTES DE PAUSA

    @Test
    void devePausarReproducao() {
        Historico historico =
                reproducaoService.assistir(usuario, filme);

        assertTrue(reproducaoService.pausar(historico));
        assertFalse(reproducaoService.estaReproduzindo(historico));
    }

    @Test
    void naoDevePausarHistoricoNulo() {
        assertFalse(reproducaoService.pausar(null));
    }

    @Test
    void naoDevePausarReproducaoQueJaEstaPausada() {
        Historico historico =
                reproducaoService.assistir(usuario, filme);

        reproducaoService.pausar(historico);

        assertFalse(reproducaoService.pausar(historico));
    }

    // TESTES DE PARADA

    @Test
    void devePararReproducao() {
        Historico historico =
                reproducaoService.assistir(usuario, filme);

        assertTrue(reproducaoService.parar(historico));
        assertFalse(reproducaoService.estaReproduzindo(historico));
    }

    @Test
    void naoDevePararHistoricoNulo() {
        assertFalse(reproducaoService.parar(null));
    }

    @Test
    void naoDevePararHistoricoNaoRegistrado() {
        Historico historico = new Historico(filme, 0, 0);

        assertFalse(reproducaoService.parar(historico));
    }

    // TESTES DE ATUALIZAÇÃO DE PROGRESSO

    @Test
    void deveAtualizarProgressoDuranteReproducao() {
        Historico historico =
                reproducaoService.assistir(usuario, filme);

        assertTrue(
                reproducaoService.atualizarProgresso(
                        historico, 45.5, 0
                )
        );

        assertEquals(45.5, historico.consultarProgresso());
    }

    @Test
    void naoDeveAtualizarProgressoComHistoricoNulo() {
        assertFalse(
                reproducaoService.atualizarProgresso(null, 45, 0)
        );
    }

    @Test
    void naoDeveAtualizarProgressoComReproducaoPausada() {
        Historico historico =
                reproducaoService.assistir(usuario, filme);

        reproducaoService.pausar(historico);

        assertFalse(
                reproducaoService.atualizarProgresso(
                        historico, 45, 0
                )
        );
    }

    @Test
    void naoDeveAceitarProgressoNegativo() {
        Historico historico =
                reproducaoService.assistir(usuario, filme);

        assertThrows(
                IllegalArgumentException.class,
                () -> reproducaoService.atualizarProgresso(
                        historico, -10, 0
                )
        );
    }

    // TESTES DE RETOMADA

    @Test
    void deveRetomarReproducaoComProgressoSalvo() {
        Historico historico =
                reproducaoService.assistir(usuario, filme);

        reproducaoService.atualizarProgresso(
                historico, 45, 0
        );

        reproducaoService.pausar(historico);

        assertTrue(
                reproducaoService.retomar(usuario, historico)
        );

        assertTrue(reproducaoService.estaReproduzindo(historico));
    }

    @Test
    void naoDeveRetomarComUsuarioNulo() {
        Historico historico =
                reproducaoService.assistir(usuario, filme);

        assertFalse(
                reproducaoService.retomar(null, historico)
        );
    }

    @Test
    void naoDeveRetomarComHistoricoNulo() {
        assertFalse(
                reproducaoService.retomar(usuario, null)
        );
    }

    @Test
    void naoDeveRetomarHistoricoDeOutroUsuario() {
        Historico historico =
                reproducaoService.assistir(usuario, filme);

        Usuario outroUsuario = new Usuario(
                "Outro",
                "outro@email.com",
                "123456"
        );

        outroUsuario.adicionarAssinatura(
                new Assinatura(
                        "Premium",
                        39.90,
                        LocalDate.now().minusDays(10),
                        LocalDate.now().plusDays(30)
                )
        );

        assertFalse(
                reproducaoService.retomar(outroUsuario, historico)
        );
    }

    @Test
    void naoDeveRetomarConteudoConcluido() {
        Historico historico =
                reproducaoService.assistir(usuario, filme);

        historico.marcarComoAssistido();

        assertFalse(
                reproducaoService.retomar(usuario, historico)
        );
    }

    @Test
    void naoDeveRetomarSemProgressoSalvo() {
        Historico historico =
                reproducaoService.assistir(usuario, filme);

        assertFalse(
                reproducaoService.retomar(usuario, historico)
        );
    }
}
