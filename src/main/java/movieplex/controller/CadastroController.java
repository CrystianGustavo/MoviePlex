
package movieplex.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import movieplex.model.Usuario;
import movieplex.service.UsuarioService;

@Controller
public class CadastroController {

    private final UsuarioService usuarioService;

    public CadastroController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/cadastro")
    public String exibirCadastro() {
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrarUsuario(
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam String senha,
            @RequestParam String confirmarSenha,
            org.springframework.ui.Model model,
            RedirectAttributes redirectAttributes) {

        if (nome.isBlank() || email.isBlank()
                || senha.isBlank() || confirmarSenha.isBlank()) {

            model.addAttribute("erro", "Preencha todos os campos.");
            return "cadastro";
        }

        if (!senha.equals(confirmarSenha)) {
            model.addAttribute("erro", "As senhas não coincidem.");
            return "cadastro";
        }

        Usuario usuario = new Usuario(nome, email, senha);

        boolean cadastrado = usuarioService.cadastrarUsuario(usuario);

        if (!cadastrado) {
            model.addAttribute(
                "erro",
                "Este e-mail já está cadastrado ou é inválido."
            );

            return "cadastro";
        }

        redirectAttributes.addFlashAttribute(
            "sucesso",
            "Conta criada com sucesso! Você já pode entrar."
        );

        return "redirect:/";
    }
}
