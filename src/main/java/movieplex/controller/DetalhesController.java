
package movieplex.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.servlet.http.HttpSession;
import movieplex.model.ConteudoSalvo;
import movieplex.model.Historico;
import movieplex.model.Usuario;
import movieplex.service.ConteudoSalvoService;
import movieplex.service.HistoricoService;
import movieplex.service.TmdbService;
import movieplex.service.VideoService;

@Controller
@RequestMapping("/detalhes")
public class DetalhesController {

    private final TmdbService tmdbService;
    private final ConteudoSalvoService conteudoSalvoService;
    private final HistoricoService historicoService;
    private final VideoService videoService;

    public DetalhesController(
            TmdbService tmdbService,
            ConteudoSalvoService conteudoSalvoService,
            HistoricoService historicoService,
            VideoService videoService) {

        this.tmdbService = tmdbService;
        this.conteudoSalvoService = conteudoSalvoService;
        this.historicoService = historicoService;
        this.videoService = videoService;
    }

    @GetMapping("/{id}")
    public String exibirDetalhes(
            @PathVariable Long id,
            @RequestParam String tipo,
            HttpSession session,
            Model model) {

        Usuario usuario =
                (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null) {
            return "redirect:/";
        }

        if (id == null || id <= 0 ||
                (!tipo.equals("movie") && !tipo.equals("tv"))) {
            return "redirect:/inicio";
        }

        try {

            JsonNode detalhes;

            if (tipo.equals("movie")) {
                detalhes = tmdbService.buscarDetalhesFilme(id);
            } else {
                detalhes = tmdbService.buscarDetalhesSerie(id);
            }

            if (detalhes == null || detalhes.isEmpty()) {
                return "redirect:/inicio";
            }

            // Verificar se o conteúdo está na Minha Lista
            Optional<ConteudoSalvo> conteudoSalvo =
                    conteudoSalvoService.buscar(
                            usuario.getEmail(),
                            id,
                            tipo
                    );

            boolean estaSalvo = conteudoSalvo.isPresent();

            Long conteudoSalvoId = conteudoSalvo
                    .map(ConteudoSalvo::getId)
                    .orElse(null);

            // Recuperar progresso do histórico
            Optional<Historico> historico =
                    historicoService.buscar(
                            usuario.getEmail(),
                            id,
                            tipo
                    );

            double progresso = historico
                    .map(Historico::getTempo)
                    .orElse(0.0);

            boolean jaAssistido = historico
                    .map(Historico::isConcluido)
                    .orElse(false);

            // Verificar se o conteúdo já foi iniciado
            boolean foiIniciado =
                    historico.isPresent()
                    && progresso > 0
                    && !jaAssistido;

            // Localizar trailer correspondente
            String trailer = videoService.buscarTrailer(id);

            boolean possuiTrailer = trailer != null;

            // Enviar informações para a página
            model.addAttribute("conteudo", detalhes);
            model.addAttribute("tipo", tipo);
            model.addAttribute("nome", usuario.getNome());

            model.addAttribute("estaSalvo", estaSalvo);
            model.addAttribute("conteudoSalvoId", conteudoSalvoId);

            model.addAttribute("progresso", progresso);
            model.addAttribute("jaAssistido", jaAssistido);
            model.addAttribute("foiIniciado", foiIniciado);

            model.addAttribute("trailer", trailer);
            model.addAttribute("possuiTrailer", possuiTrailer);

            return "detalhes";

        } catch (Exception e) {

            e.printStackTrace();
            return "redirect:/inicio";
        }
    }
}
