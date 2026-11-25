
package movieplex.controller;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import movieplex.model.Assinatura;
import movieplex.model.Usuario;
import movieplex.service.UsuarioService;

@Controller
public class AssinaturaController {

    private final UsuarioService usuarioService;

    public AssinaturaController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Exibir os planos disponíveis
    @GetMapping("/planos")
    public String exibirPlanos(
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null) {
            return "redirect:/";
        }

        model.addAttribute("nome", usuario.getNome());

        if (usuario.getAssinatura() != null) {
            model.addAttribute(
                "possuiAssinatura",
                usuario.getAssinatura().validarAssinatura()
            );
        } else {
            model.addAttribute("possuiAssinatura", false);
        }

        return "planos";
    }

    // Contratar um plano
    @PostMapping("/assinatura/contratar")
    public String contratarPlano(
            @RequestParam String plano,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null) {
            return "redirect:/";
        }

        double valor;

        switch (plano) {
            case "BASICO":
                valor = 14.90;
                break;

            case "PADRAO":
                valor = 24.90;
                break;

            case "PREMIUM":
                valor = 34.90;
                break;

            default:
                redirectAttributes.addFlashAttribute(
                    "erro",
                    "Plano inválido."
                );
                return "redirect:/planos";
        }

        LocalDate dataInicio = LocalDate.now();
        LocalDate dataFim = dataInicio.plusDays(30);

        Assinatura assinatura = new Assinatura(
            plano,
            valor,
            dataInicio,
            dataFim
        );

        boolean sucesso = usuarioService.realizarAssinatura(
            usuario.getEmail(),
            assinatura
        );

        if (!sucesso) {
            redirectAttributes.addFlashAttribute(
                "erro",
                "Não foi possível contratar o plano. Verifique se você já possui uma assinatura."
            );

            return "redirect:/planos";
        }

        // Atualiza o usuário armazenado na sessão
        usuario.adicionarAssinatura(assinatura);

        redirectAttributes.addFlashAttribute(
            "sucesso",
            "Sua assinatura foi realizada com sucesso!"
        );

        return "redirect:/assinatura/sucesso";
    }

    // Página de confirmação
    @GetMapping("/assinatura/sucesso")
    public String assinaturaSucesso(
            HttpSession session,
            Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null) {
            return "redirect:/";
        }

        model.addAttribute("nome", usuario.getNome());
        model.addAttribute("assinatura", usuario.getAssinatura());

        return "assinatura-sucesso";
    }


    @GetMapping("/minha-assinatura")
    public String minhaAssinatura(HttpSession session, Model model) {

        Usuario usuarioSessao =
                (Usuario) session.getAttribute("usuarioLogado");

        if (usuarioSessao == null) {
            return "redirect:/";
        }

        // Busca os dados atualizados no banco
        Usuario usuario = usuarioService.buscarUsuario(
                usuarioSessao.getEmail()
        );

        if (usuario == null) {
            session.invalidate();
            return "redirect:/";
        }

        session.setAttribute("usuarioLogado", usuario);

        if (usuario.getAssinatura() == null) {
            return "redirect:/planos";
        }

        model.addAttribute("nome", usuario.getNome());
        model.addAttribute("assinatura", usuario.getAssinatura());

        return "minha-assinatura";
    }


    @PostMapping("/assinatura/alterar")
    public String alterarPlano(
            @RequestParam String plano,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Usuario usuario =
                (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null) {
            return "redirect:/";
        }

        double valor;

        switch (plano) {
            case "BASICO":
                valor = 14.90;
                break;

            case "PADRAO":
                valor = 24.90;
                break;

            case "PREMIUM":
                valor = 34.90;
                break;

            default:
                redirectAttributes.addFlashAttribute(
                        "erro", "Plano inválido."
                );
                return "redirect:/minha-assinatura";
        }

        boolean sucesso = usuarioService.alterarAssinatura(
                usuario.getEmail(),
                plano,
                valor
        );

        if (sucesso) {
            redirectAttributes.addFlashAttribute(
                    "sucesso", "Seu plano foi alterado com sucesso!"
            );
        } else {
            redirectAttributes.addFlashAttribute(
                    "erro", "Não foi possível alterar seu plano."
            );
        }

        return "redirect:/minha-assinatura";
    }


    @PostMapping("/assinatura/cancelar")
    public String cancelarAssinatura(
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Usuario usuario =
                (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null) {
            return "redirect:/";
        }

        boolean sucesso = usuarioService.cancelarAssinatura(
                usuario.getEmail()
        );

        if (sucesso) {
            redirectAttributes.addFlashAttribute(
                    "sucesso", "Sua assinatura foi cancelada."
            );
        } else {
            redirectAttributes.addFlashAttribute(
                    "erro", "Não foi possível cancelar sua assinatura."
            );
        }

        return "redirect:/minha-assinatura";
    }
}
