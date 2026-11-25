package movieplex.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import movieplex.model.Usuario;
import movieplex.service.HistoricoService;

@Controller
@RequestMapping("/historico")
public class HistoricoController {

    private final HistoricoService historicoService;

    public HistoricoController(HistoricoService historicoService) {
        this.historicoService = historicoService;
    }

    // Exibir histórico
    @GetMapping
    public String exibirHistorico(HttpSession session, Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null) {
            return "redirect:/";
        }

        model.addAttribute("nome", usuario.getNome());

        model.addAttribute(
                "historicos",
                historicoService.listarTodos(usuario.getEmail())
        );

        model.addAttribute(
                "emAndamento",
                historicoService.listarEmAndamento(usuario.getEmail())
        );

        return "historico";
    }

    // Marcar conteúdo como assistido
    @PostMapping("/concluir")
    public String concluir(
            @RequestParam Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null) {
            return "redirect:/";
        }

        try {
            historicoService.marcarComoAssistido(usuario.getEmail(), id);

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Conteúdo marcado como assistido!"
            );

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    e.getMessage()
            );
        }

        return "redirect:/historico";
    }

    // Remover conteúdo do histórico
    @PostMapping("/remover")
    public String remover(
            @RequestParam Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null) {
            return "redirect:/";
        }

        try {
            historicoService.remover(usuario.getEmail(), id);

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Registro removido do histórico!"
            );

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    e.getMessage()
            );
        }

        return "redirect:/historico";
    }
}
