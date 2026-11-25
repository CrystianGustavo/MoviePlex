
package movieplex.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.servlet.http.HttpSession;
import movieplex.model.Historico;
import movieplex.model.Usuario;
import movieplex.service.HistoricoService;
import movieplex.service.TmdbService;
import movieplex.service.VideoService;

@Controller
@RequestMapping("/assistir")
public class PlayerController {

    private final VideoService videoService;
    private final TmdbService tmdbService;
    private final HistoricoService historicoService;

    public PlayerController(
            VideoService videoService,
            TmdbService tmdbService,
            HistoricoService historicoService) {

        this.videoService = videoService;
        this.tmdbService = tmdbService;
        this.historicoService = historicoService;
    }

    @GetMapping("/{id}")
    public String assistir(
            @PathVariable Long id,
            @RequestParam String tipo,
            HttpSession session,
            Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null) {
            return "redirect:/";
        }

        if (id == null || id <= 0 ||
                (!"movie".equals(tipo) && !"tv".equals(tipo))) {
            return "redirect:/inicio";
        }

        try {

            String videoUrl = videoService.buscarTrailer(id);

            if (videoUrl == null) {
                return "redirect:/detalhes/" + id + "?tipo=" + tipo;
            }

            JsonNode conteudo;

            if ("movie".equals(tipo)) {
                conteudo = tmdbService.buscarDetalhesFilme(id);
            } else {
                conteudo = tmdbService.buscarDetalhesSerie(id);
            }

            if (conteudo == null || conteudo.isEmpty()) {
                return "redirect:/inicio";
            }

            Optional<Historico> historico = historicoService.buscar(
                    usuario.getEmail(), id, tipo
            );

            double progresso = historico
                    .map(Historico::getTempo)
                    .orElse(0.0);

            String titulo = conteudo.hasNonNull("title")
                    ? conteudo.get("title").asText()
                    : conteudo.path("name").asText("Conteúdo");

            String posterPath = conteudo.path("poster_path").asText("");

            model.addAttribute("nome", usuario.getNome());
            model.addAttribute("conteudo", conteudo);
            model.addAttribute("videoUrl", videoUrl);
            model.addAttribute("tmdbId", id);
            model.addAttribute("tipo", tipo);
            model.addAttribute("titulo", titulo);
            model.addAttribute("posterPath", posterPath);
            model.addAttribute("progresso", progresso);

            return "player";

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/inicio";
        }
    }

    @PostMapping("/progresso")
    @ResponseBody
    public ResponseEntity<Void> salvarProgresso(
            @RequestParam Long tmdbId,
            @RequestParam String tipo,
            @RequestParam String titulo,
            @RequestParam(defaultValue = "") String posterPath,
            @RequestParam double tempo,
            @RequestParam double duracao,
            HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {

            historicoService.salvarProgresso(
                    usuario.getEmail(),
                    tmdbId,
                    tipo,
                    titulo,
                    posterPath,
                    tempo,
                    duracao,
                    0,
                    0
            );

            return ResponseEntity.ok().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
