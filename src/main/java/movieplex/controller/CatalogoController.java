
package movieplex.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import movieplex.model.Usuario;
import movieplex.service.CatalogoService;
import movieplex.service.HistoricoService;

@Controller
public class CatalogoController {

    private final CatalogoService catalogoService;
    private final HistoricoService historicoService;

    public CatalogoController(
            CatalogoService catalogoService,
            HistoricoService historicoService) {

        this.catalogoService = catalogoService;
        this.historicoService = historicoService;
    }

    @GetMapping("/inicio")
    public String exibirCatalogo(HttpSession session, Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null) {
            return "redirect:/";
        }

        model.addAttribute("nome", usuario.getNome());

        // Conteúdos do catálogo
        model.addAttribute("emAlta", catalogoService.getEmAlta());

        model.addAttribute("filmes", catalogoService.getFilmesPopulares());

        model.addAttribute("series", catalogoService.getSeriesPopulares());

        model.addAttribute("lancamentosFilmes",
                catalogoService.getLancamentosFilmes());

        model.addAttribute("lancamentosSeries",
                catalogoService.getLancamentosSeries());

        // Conteúdos iniciados e ainda não concluídos
        model.addAttribute("continuarAssistindo",
                historicoService.listarEmAndamento(usuario.getEmail()));

        return "inicio";
    }
}
