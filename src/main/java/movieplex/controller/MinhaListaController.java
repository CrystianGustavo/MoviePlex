package movieplex.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import movieplex.model.ConteudoSalvo;
import movieplex.model.Usuario;
import movieplex.service.ConteudoSalvoService;

@Controller
public class MinhaListaController {

    private final ConteudoSalvoService conteudoSalvoService;

    public MinhaListaController(
            ConteudoSalvoService conteudoSalvoService) {
        this.conteudoSalvoService = conteudoSalvoService;
    }

    // Página Minha Lista
    @GetMapping("/minha-lista")
    public String minhaLista(
            HttpSession session,
            Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null) {
            return "redirect:/";
        }

        model.addAttribute("nome", usuario.getNome());

        model.addAttribute(
                "conteudos",
                conteudoSalvoService.listar(usuario.getEmail())
        );

        return "minha-lista";
    }

    // Adicionar conteúdo
    @PostMapping("/minha-lista/adicionar")
    public String adicionar(
            @RequestParam Long tmdbId,
            @RequestParam String tipo,
            @RequestParam String titulo,
            @RequestParam(required = false) String posterPath,
            @RequestParam(required = false) String backdropPath,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null) {
            return "redirect:/";
        }

        ConteudoSalvo conteudo = new ConteudoSalvo(
                tmdbId,
                tipo,
                titulo,
                posterPath,
                backdropPath
        );

        boolean adicionado = conteudoSalvoService.adicionar(
                usuario.getEmail(),
                conteudo
        );

        if (adicionado) {
            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Conteúdo adicionado à sua lista!"
            );
        } else {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Este conteúdo já está na sua lista ou os dados são inválidos."
            );
        }

        return "redirect:/detalhes/" + tmdbId + "?tipo=" + tipo;
    }

    // Remover conteúdo
    @PostMapping("/minha-lista/remover")
    public String remover(
            @RequestParam Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null) {
            return "redirect:/";
        }

        boolean removido = conteudoSalvoService.remover(
                usuario.getEmail(),
                id
        );

        if (removido) {
            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Conteúdo removido da sua lista!"
            );
        } else {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Não foi possível remover este conteúdo."
            );
        }

        return "redirect:/minha-lista";
    }
}
