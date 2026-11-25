package movieplex.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import movieplex.model.Usuario;
import movieplex.service.UsuarioService;

@Controller
public class EditarPerfilController {

    private final UsuarioService usuarioService;

    public EditarPerfilController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/minha-conta/editar")
    public String exibirFormulario(HttpSession session, Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null) {
            return "redirect:/";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("nome", usuario.getNome());

        return "editar-perfil";
    }

    @PostMapping("/minha-conta/editar")
    public String atualizarPerfil(
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam(required = false) String senhaAtual,
            @RequestParam(required = false) String novaSenha,
            @RequestParam(required = false) String confirmarSenha,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null) {
            return "redirect:/";
        }

        if (nome == null || nome.isBlank()
                || email == null || email.isBlank()
                || !email.contains("@")) {

            model.addAttribute("erro", "Preencha corretamente o nome e o e-mail.");
            model.addAttribute("usuario", usuario);
            model.addAttribute("nome", usuario.getNome());

            return "editar-perfil";
        }

        if (novaSenha != null && !novaSenha.isBlank()) {

            if (!novaSenha.equals(confirmarSenha)) {
                model.addAttribute("erro", "As senhas não coincidem.");
                model.addAttribute("usuario", usuario);
                model.addAttribute("nome", usuario.getNome());

                return "editar-perfil";
            }

            if (novaSenha.length() < 6) {
                model.addAttribute("erro", "A nova senha deve ter pelo menos 6 caracteres.");
                model.addAttribute("usuario", usuario);
                model.addAttribute("nome", usuario.getNome());

                return "editar-perfil";
            }
        }

        boolean sucesso = usuarioService.alterarCadastro(
                usuario.getId(),
                nome,
                email,
                senhaAtual,
                novaSenha
        );

        if (!sucesso) {
            model.addAttribute("erro",
                    "Não foi possível atualizar. Verifique sua senha atual ou se o e-mail já está cadastrado.");

            model.addAttribute("usuario", usuario);
            model.addAttribute("nome", usuario.getNome());

            return "editar-perfil";
        }

        usuario.setNome(nome.trim());
        usuario.setEmail(email.trim());

        session.setAttribute("usuarioLogado", usuario);

        redirectAttributes.addFlashAttribute(
                "sucesso",
                "Seus dados foram atualizados com sucesso!"
        );

        return "redirect:/minha-conta";
    }
}
