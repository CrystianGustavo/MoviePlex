package movieplex.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import movieplex.model.Usuario;

@Controller
public class MinhaContaController {

    @GetMapping("/minha-conta")
    public String exibirMinhaConta(HttpSession session, Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null) {
            return "redirect:/";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("nome", usuario.getNome());

        return "minha-conta";
    }
}
