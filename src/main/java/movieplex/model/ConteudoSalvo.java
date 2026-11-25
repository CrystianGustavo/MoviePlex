
package movieplex.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "conteudos_salvos",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_usuario_conteudo",
            columnNames = {"usuario_id", "tmdb_id", "tipo"}
        )
    }
)
public class ConteudoSalvo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tmdb_id", nullable = false)
    private Long tmdbId;

    @Column(nullable = false, length = 10)
    private String tipo;

    @Column(nullable = false, length = 255)
    private String titulo;

    @Column(name = "poster_path", length = 500)
    private String posterPath;

    @Column(name = "backdrop_path", length = 500)
    private String backdropPath;

    @Column(name = "data_adicao", nullable = false)
    private LocalDateTime dataAdicao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public ConteudoSalvo() {
    }

    public ConteudoSalvo(
            Long tmdbId,
            String tipo,
            String titulo,
            String posterPath,
            String backdropPath) {

        this.tmdbId = tmdbId;
        this.tipo = tipo;
        this.titulo = titulo;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
    }

    @PrePersist
    public void prePersist() {
        if (dataAdicao == null) {
            dataAdicao = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public Long getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(Long tmdbId) {
        this.tmdbId = tmdbId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public LocalDateTime getDataAdicao() {
        return dataAdicao;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
