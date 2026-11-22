
package main.java.movieplex.service;

import movieplex.model.Usuario;
import movieplex.model.Assinatura;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class UsuarioService {

    private List<Usuario> usuarios;

    public UsuarioService() {
        this.usuarios = new ArrayList<>();
    }

    // R1 - Cadastrar usuário
    public boolean cadastrarUsuario(Usuario usuario) {

        if (usuario == null || usuario.getEmail() == null
                || usuario.getEmail().isBlank()) {
            return false;
        }

        for (Usuario existente : usuarios) {
            if (existente.getEmail().equalsIgnoreCase(usuario.getEmail())) {
                return false;
            }
        }

        usuarios.add(usuario);
        return true;
    }

    // R2 - Alterar cadastro
    public boolean alterarCadastro(String email,
                                   String novoNome,
                                   String novoEmail,
                                   String novaSenha) {

        Usuario usuario = buscarUsuario(email);

        if (usuario == null || novoEmail == null
                || novoEmail.isBlank()) {
            return false;
        }

        Usuario outro = buscarUsuario(novoEmail);

        if (outro != null && outro != usuario) {
            return false;
        }

        usuario.atualizarPerfil(novoNome, novoEmail, novaSenha);
        return true;
    }

    // R3 - Excluir cadastro
    public boolean excluirCadastro(String email) {

        Usuario usuario = buscarUsuario(email);

        if (usuario == null) {
            return false;
        }

        usuarios.remove(usuario);
        return true;
    }

    // R4 - Realizar login
    public Usuario realizarLogin(String email, String senha) {

        Usuario usuario = buscarUsuario(email);

        if (usuario == null) {
            return null;
        }

        if (usuario.realizarLogin(email, senha)) {
            return usuario;
        }

        return null;
    }

    // R5 - Realizar assinatura
    public boolean realizarAssinatura(String email,
                                      Assinatura assinatura) {

        Usuario usuario = buscarUsuario(email);

        if (usuario == null || assinatura == null
                || !assinatura.validarAssinatura()) {
            return false;
        }

        usuario.adicionarAssinatura(assinatura);
        return true;
    }

    // R6 - Alterar assinatura
    public boolean alterarAssinatura(String email,
                                     String novoPlano,
                                     double novoValor) {

        Usuario usuario = buscarUsuario(email);

        if (usuario == null || novoPlano == null
                || novoPlano.isBlank() || novoValor <= 0) {
            return false;
        }

        for (Assinatura assinatura : usuario.getAssinaturas()) {
            if (assinatura.validarAssinatura()) {
                assinatura.alterarPlano(novoPlano, novoValor);
                return true;
            }
        }

        return false;
    }

    // R7 - Cancelar assinatura
    public boolean cancelarAssinatura(String email) {

        Usuario usuario = buscarUsuario(email);

        if (usuario == null) {
            return false;
        }

        for (Assinatura assinatura : usuario.getAssinaturas()) {
            if (assinatura.isAtivo()) {
                assinatura.cancelarAssinatura();
                return true;
            }
        }

        return false;
    }

    // Buscar usuário
    public Usuario buscarUsuario(String email) {

        if (email == null) {
            return null;
        }

        for (Usuario usuario : usuarios) {
            if (usuario.getEmail().equalsIgnoreCase(email)) {
                return usuario;
            }
        }

        return null;
    }

    // Listar usuários
    public List<Usuario> listarUsuarios() {
        return new ArrayList<>(usuarios);
    }
}
