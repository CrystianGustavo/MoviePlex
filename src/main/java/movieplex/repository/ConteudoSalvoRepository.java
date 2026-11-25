package movieplex.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import movieplex.model.ConteudoSalvo;

@Repository
public interface ConteudoSalvoRepository
        extends JpaRepository<ConteudoSalvo, Long> {

    List<ConteudoSalvo> findByUsuarioIdOrderByDataAdicaoDesc(Long usuarioId);

    Optional<ConteudoSalvo> findByUsuarioIdAndTmdbIdAndTipo(
            Long usuarioId,
            Long tmdbId,
            String tipo
    );

    boolean existsByUsuarioIdAndTmdbIdAndTipo(
            Long usuarioId,
            Long tmdbId,
            String tipo
    );

    Optional<ConteudoSalvo> findByIdAndUsuarioId(
            Long id,
            Long usuarioId
    );
}
