
package movieplex.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import movieplex.model.ConteudoSalvo;
import movieplex.model.Usuario;
import movieplex.repository.ConteudoSalvoRepository;
import movieplex.repository.UsuarioRepository;

@Service
public class ConteudoSalvoService {

    private final ConteudoSalvoRepository conteudoSalvoRepository;
    private final UsuarioRepository usuarioRepository;

    public ConteudoSalvoService(
            ConteudoSalvoRepository conteudoSalvoRepository,
            UsuarioRepository usuarioRepository) {

        this.conteudoSalvoRepository = conteudoSalvoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Adicionar conteúdo à Minha Lista
    @Transactional
    public boolean adicionar(String email, ConteudoSalvo conteudo) {

        if (email == null || conteudo == null
                || conteudo.getTmdbId() == null
                || conteudo.getTmdbId() <= 0
                || conteudo.getTitulo() == null
                || conteudo.getTitulo().isBlank()
                || conteudo.getTipo() == null
                || (!conteudo.getTipo().equals("movie")
                    && !conteudo.getTipo().equals("tv"))) {
            return false;
        }

        Usuario usuario = usuarioRepository
                .findByEmailIgnoreCase(email)
                .orElse(null);

        if (usuario == null) {
            return false;
        }

        boolean jaExiste = conteudoSalvoRepository
                .existsByUsuarioIdAndTmdbIdAndTipo(
                        usuario.getId(),
                        conteudo.getTmdbId(),
                        conteudo.getTipo()
                );

        if (jaExiste) {
            return false;
        }

        conteudo.setUsuario(usuario);
        conteudoSalvoRepository.save(conteudo);

        return true;
    }

    // Listar conteúdos salvos pelo usuário
    @Transactional(readOnly = true)
    public List<ConteudoSalvo> listar(String email) {

        Usuario usuario = usuarioRepository
                .findByEmailIgnoreCase(email)
                .orElse(null);

        if (usuario == null) {
            return List.of();
        }

        return conteudoSalvoRepository
                .findByUsuarioIdOrderByDataAdicaoDesc(usuario.getId());
    }

    // Verificar se um conteúdo já está salvo
    @Transactional(readOnly = true)
    public boolean estaSalvo(String email, Long tmdbId, String tipo) {

        if (email == null || tmdbId == null || tipo == null) {
            return false;
        }

        Usuario usuario = usuarioRepository
                .findByEmailIgnoreCase(email)
                .orElse(null);

        if (usuario == null) {
            return false;
        }

        return conteudoSalvoRepository
                .existsByUsuarioIdAndTmdbIdAndTipo(
                        usuario.getId(),
                        tmdbId,
                        tipo
                );
    }

    // Buscar conteúdo salvo pelo usuário
    @Transactional(readOnly = true)
    public Optional<ConteudoSalvo> buscar(
            String email,
            Long tmdbId,
            String tipo) {

        if (email == null || tmdbId == null || tipo == null) {
            return Optional.empty();
        }

        Usuario usuario = usuarioRepository
                .findByEmailIgnoreCase(email)
                .orElse(null);

        if (usuario == null) {
            return Optional.empty();
        }

        return conteudoSalvoRepository
                .findByUsuarioIdAndTmdbIdAndTipo(
                        usuario.getId(),
                        tmdbId,
                        tipo
                );
    }

    // Remover conteúdo da Minha Lista
    @Transactional
    public boolean remover(String email, Long id) {

        if (email == null || id == null) {
            return false;
        }

        Usuario usuario = usuarioRepository
                .findByEmailIgnoreCase(email)
                .orElse(null);

        if (usuario == null) {
            return false;
        }

        ConteudoSalvo conteudo = conteudoSalvoRepository
                .findByIdAndUsuarioId(id, usuario.getId())
                .orElse(null);

        if (conteudo == null) {
            return false;
        }

        conteudoSalvoRepository.delete(conteudo);

        return true;
    }
}
