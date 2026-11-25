package movieplex.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import movieplex.model.Historico;
import movieplex.model.Usuario;
import movieplex.repository.HistoricoRepository;
import movieplex.repository.UsuarioRepository;

@Service
public class HistoricoService {

    private final HistoricoRepository historicoRepository;
    private final UsuarioRepository usuarioRepository;

    public HistoricoService(
            HistoricoRepository historicoRepository,
            UsuarioRepository usuarioRepository) {

        this.historicoRepository = historicoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Registrar ou atualizar o progresso
    @Transactional
    public Historico salvarProgresso(
            String email,
            Long tmdbId,
            String tipo,
            String titulo,
            String posterPath,
            double tempo,
            double duracao,
            int temporada,
            int episodio) {

        if (email == null || email.isBlank()
                || tmdbId == null || tmdbId <= 0
                || titulo == null || titulo.isBlank()
                || tipo == null
                || (!tipo.equalsIgnoreCase("movie")
                    && !tipo.equalsIgnoreCase("tv"))
                || tempo < 0
                || duracao < 0
                || temporada < 0
                || episodio < 0
                || (duracao > 0 && tempo > duracao)) {

            throw new IllegalArgumentException(
                    "Dados inválidos para registrar o histórico."
            );
        }

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Usuário não encontrado."
                ));

        String tipoNormalizado = tipo.toLowerCase();

        Historico historico = historicoRepository
                .findByUsuarioIdAndTmdbIdAndTipo(
                        usuario.getId(),
                        tmdbId,
                        tipoNormalizado
                )
                .orElseGet(() -> new Historico(
                        tmdbId,
                        tipoNormalizado,
                        titulo,
                        posterPath,
                        duracao
                ));

        historico.setUsuario(usuario);
        historico.setTitulo(titulo);
        historico.setPosterPath(posterPath);
        historico.setDuracao(duracao);

        historico.atualizarProgresso(
                tempo,
                temporada,
                episodio
        );

        return historicoRepository.save(historico);
    }

    // Listar conteúdos em andamento
    @Transactional(readOnly = true)
    public List<Historico> listarEmAndamento(String email) {

        Usuario usuario = buscarUsuario(email);

        return historicoRepository
                .findByUsuarioIdAndConcluidoFalseOrderByDataAtualizacaoDesc(
                        usuario.getId()
                );
    }

    // Listar todo o histórico
    @Transactional(readOnly = true)
    public List<Historico> listarTodos(String email) {

        Usuario usuario = buscarUsuario(email);

        return historicoRepository
                .findByUsuarioIdOrderByDataAtualizacaoDesc(
                        usuario.getId()
                );
    }

    // Marcar conteúdo como assistido
    @Transactional
    public Historico marcarComoAssistido(String email, Long id) {

        Usuario usuario = buscarUsuario(email);

        Historico historico = historicoRepository
                .findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Histórico não encontrado."
                ));

        historico.marcarComoAssistido();

        return historicoRepository.save(historico);
    }

    // Remover registro do histórico
    @Transactional
    public void remover(String email, Long id) {

        Usuario usuario = buscarUsuario(email);

        Historico historico = historicoRepository
                .findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Histórico não encontrado."
                ));

        historicoRepository.delete(historico);
    }

    // Buscar usuário pelo e-mail
    private Usuario buscarUsuario(String email) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "E-mail do usuário não informado."
            );
        }

        return usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Usuário não encontrado."
                ));
    }


    @Transactional(readOnly = true)
    public java.util.Optional<Historico> buscar(
            String email,
            Long tmdbId,
            String tipo) {

        Usuario usuario = buscarUsuario(email);

        return historicoRepository.findByUsuarioIdAndTmdbIdAndTipo(
                usuario.getId(),
                tmdbId,
                tipo
        );
    }
}
