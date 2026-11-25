
package movieplex.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import movieplex.model.Assinatura;
import movieplex.model.Usuario;
import movieplex.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // R1 - Cadastrar usuário
    @Transactional
    public boolean cadastrarUsuario(Usuario usuario) {

        if (usuario == null
                || usuario.getEmail() == null
                || usuario.getEmail().isBlank()
                || usuario.getSenha() == null
                || usuario.getSenha().isBlank()
                || usuario.getNome() == null
                || usuario.getNome().isBlank()) {
            return false;
        }

        if (usuarioRepository.existsByEmailIgnoreCase(
                usuario.getEmail().trim())) {
            return false;
        }

        usuario.setNome(usuario.getNome().trim());
        usuario.setEmail(usuario.getEmail().trim());
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        usuarioRepository.save(usuario);

        return true;
    }

    // R2 - Alterar cadastro
    @Transactional
    public boolean alterarCadastro(
            Long id,
            String nome,
            String email,
            String senhaAtual,
            String novaSenha) {

        Usuario usuario = usuarioRepository.findById(id).orElse(null);

        if (usuario == null || nome == null || email == null) {
            return false;
        }

        nome = nome.trim();
        email = email.trim();

        if (nome.isBlank() || email.isBlank()) {
            return false;
        }

        Usuario usuarioComEmail = buscarUsuario(email);

        if (usuarioComEmail != null
                && !usuarioComEmail.getId().equals(id)) {
            return false;
        }

        if (novaSenha != null && !novaSenha.isBlank()) {

            if (senhaAtual == null || senhaAtual.isBlank()) {
                return false;
            }

            if (!passwordEncoder.matches(
                    senhaAtual, usuario.getSenha())) {
                return false;
            }

            if (novaSenha.length() < 6) {
                return false;
            }

            usuario.setSenha(passwordEncoder.encode(novaSenha));
        }

        usuario.setNome(nome);
        usuario.setEmail(email);

        usuarioRepository.save(usuario);

        return true;
    }

    // R3 - Excluir cadastro
    @Transactional
    public boolean excluirCadastro(String email) {

        Usuario usuario = buscarUsuario(email);

        if (usuario == null) {
            return false;
        }

        usuarioRepository.delete(usuario);

        return true;
    }

    // R4 - Realizar login
    public Usuario realizarLogin(String email, String senha) {

        if (email == null || senha == null) {
            return null;
        }

        Usuario usuario = buscarUsuario(email);

        if (usuario == null) {
            return null;
        }

        if (passwordEncoder.matches(senha, usuario.getSenha())) {
            return usuario;
        }

        return null;
    }

    // R5 - Realizar assinatura
    @Transactional
    public boolean realizarAssinatura(
            String email,
            Assinatura novaAssinatura) {

        Usuario usuario = buscarUsuario(email);

        if (usuario == null
                || novaAssinatura == null
                || !novaAssinatura.validarAssinatura()) {
            return false;
        }

        Assinatura assinaturaExistente = usuario.getAssinatura();

        // Se já possui assinatura ativa, impede uma segunda contratação.
        if (assinaturaExistente != null
                && assinaturaExistente.validarAssinatura()) {
            return false;
        }

        // Se possui assinatura cancelada ou expirada, reativa o registro.
        if (assinaturaExistente != null) {

            assinaturaExistente.reativarAssinatura(
                    novaAssinatura.getPlano(),
                    novaAssinatura.getValor(),
                    novaAssinatura.getDataInicio(),
                    novaAssinatura.getDataFim()
            );

        } else {

            // Se nunca teve assinatura, cria uma nova.
            usuario.adicionarAssinatura(novaAssinatura);
        }

        usuarioRepository.save(usuario);

        return true;
    }

    // R6 - Alterar assinatura
    @Transactional
    public boolean alterarAssinatura(
            String email,
            String novoPlano,
            double novoValor) {

        Usuario usuario = buscarUsuario(email);

        if (usuario == null
                || novoPlano == null
                || novoValor <= 0) {
            return false;
        }

        if (!novoPlano.equals("BASICO")
                && !novoPlano.equals("PADRAO")
                && !novoPlano.equals("PREMIUM")) {
            return false;
        }

        double valorEsperado;

        switch (novoPlano) {
            case "BASICO":
                valorEsperado = 14.90;
                break;

            case "PADRAO":
                valorEsperado = 24.90;
                break;

            case "PREMIUM":
                valorEsperado = 34.90;
                break;

            default:
                return false;
        }

        if (Double.compare(novoValor, valorEsperado) != 0) {
            return false;
        }

        Assinatura assinatura = usuario.getAssinatura();

        if (assinatura == null
                || !assinatura.validarAssinatura()) {
            return false;
        }

        assinatura.alterarPlano(novoPlano, novoValor);

        usuarioRepository.save(usuario);

        return true;
    }

    // R7 - Cancelar assinatura
    @Transactional
    public boolean cancelarAssinatura(String email) {

        Usuario usuario = buscarUsuario(email);

        if (usuario == null) {
            return false;
        }

        Assinatura assinatura = usuario.getAssinatura();

        if (assinatura == null
                || !assinatura.validarAssinatura()) {
            return false;
        }

        assinatura.cancelarAssinatura();

        usuarioRepository.save(usuario);

        return true;
    }

    // Buscar usuário
    public Usuario buscarUsuario(String email) {

        if (email == null || email.isBlank()) {
            return null;
        }

        return usuarioRepository
                .findByEmailIgnoreCase(email.trim())
                .orElse(null);
    }

    // Listar usuários
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }
}
