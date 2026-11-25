
package movieplex.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import movieplex.model.Historico;

@Repository
public interface HistoricoRepository extends JpaRepository<Historico, Long> {

    // Listar conteúdos em andamento do usuário
    List<Historico> findByUsuarioIdAndConcluidoFalseOrderByDataAtualizacaoDesc(
            Long usuarioId
    );

    // Listar todo o histórico do usuário
    List<Historico> findByUsuarioIdOrderByDataAtualizacaoDesc(
            Long usuarioId
    );

    // Buscar histórico de um conteúdo específico
    Optional<Historico> findByUsuarioIdAndTmdbIdAndTipo(
            Long usuarioId,
            Long tmdbId,
            String tipo
    );

    // Buscar um registro pertencente ao usuário
    Optional<Historico> findByIdAndUsuarioId(
            Long id,
            Long usuarioId
    );
}
