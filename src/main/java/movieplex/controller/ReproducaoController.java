
package movieplex.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import movieplex.model.Historico;
import movieplex.model.Usuario;
import movieplex.service.HistoricoService;

@Controller
@RequestMapping("/reproducao")
public class ReproducaoController {

    private final HistoricoService historicoService;

    public ReproducaoController(HistoricoService historicoService) {
        this.historicoService = historicoService;
    }

    // Salvar o progresso da reprodução
    @PostMapping("/progresso")
    @ResponseBody
    public ResponseEntity<?> salvarProgresso(
            @RequestParam Long tmdbId,
            @RequestParam String tipo,
            @RequestParam String titulo,
            @RequestParam(required = false) String posterPath,
            @RequestParam double tempo,
            @RequestParam double duracao,
            @RequestParam(defaultValue = "0") int temporada,
            @RequestParam(defaultValue = "0") int episodio,
            HttpSession session) {

        Usuario usuario =
                (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "Usuário não autenticado."));
        }

        try {

            Historico historico = historicoService.salvarProgresso(
                    usuario.getEmail(),
                    tmdbId,
                    tipo,
                    titulo,
                    posterPath,
                    tempo,
                    duracao,
                    temporada,
                    episodio
            );

            return ResponseEntity.ok(Map.of(
                    "mensagem", "Progresso salvo com sucesso.",
                    "progresso", historico.getTempo(),
                    "porcentagem", historico.calcularPorcentagem(),
                    "concluido", historico.isConcluido()
            ));

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of("erro", e.getMessage()));

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .body(Map.of("erro", "Não foi possível salvar o progresso."));
        }
    }

    // Marcar conteúdo como assistido
    @PostMapping("/concluir")
    @ResponseBody
    public ResponseEntity<?> concluir(
            @RequestParam Long tmdbId,
            @RequestParam String tipo,
            HttpSession session) {

        Usuario usuario =
                (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "Usuário não autenticado."));
        }

        try {

            Historico historico = historicoService.buscar(
                    usuario.getEmail(),
                    tmdbId,
                    tipo
            ).orElse(null);

            if (historico == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("erro", "Histórico não encontrado."));
            }

            historicoService.marcarComoAssistido(
                    usuario.getEmail(),
                    historico.getId()
            );

            return ResponseEntity.ok(Map.of(
                    "mensagem", "Conteúdo marcado como assistido."
            ));

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .body(Map.of("erro", "Não foi possível concluir o conteúdo."));
        }
    }
}
