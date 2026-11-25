
package movieplex.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.servlet.http.HttpSession;
import movieplex.model.ConteudoCatalogo;
import movieplex.model.Usuario;
import movieplex.service.CatalogoService;
import movieplex.service.TmdbService;

@Controller
public class PesquisaController {

    private final TmdbService tmdbService;
    private final CatalogoService catalogoService;

    public PesquisaController(
            TmdbService tmdbService,
            CatalogoService catalogoService) {

        this.tmdbService = tmdbService;
        this.catalogoService = catalogoService;
    }

    @GetMapping("/pesquisar")
    public String pesquisar(
            @RequestParam(name = "q", defaultValue = "") String consulta,
            HttpSession session,
            Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null) {
            return "redirect:/";
        }

        model.addAttribute("nome", usuario.getNome());
        model.addAttribute("consulta", consulta);
        model.addAttribute("erroPesquisa", false);

        if (consulta.isBlank()) {
            model.addAttribute("resultados", List.of());
            return "pesquisa";
        }

        try {
            JsonNode resposta = tmdbService.pesquisarTudo(consulta);

            List<ConteudoCatalogo> resultados =
                    catalogoService.converterResultadosPesquisa(resposta);

            model.addAttribute("resultados", resultados);

        } catch (Exception e) {
            System.err.println("Erro ao pesquisar conteúdo: " + e.getMessage());
            model.addAttribute("resultados", List.of());
            model.addAttribute("erroPesquisa", true);
        }

        return "pesquisa";
    }
}
