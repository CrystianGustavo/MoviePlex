
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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "historicos",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_usuario_historico",
            columnNames = {"usuario_id", "tmdb_id", "tipo"}
        )
    }
)
public class Historico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tmdb_id")
    private Long tmdbId;

    @Column(length = 10)
    private String tipo;

    @Column(length = 255)
    private String titulo;

    @Column(name = "poster_path", length = 500)
    private String posterPath;

    @Column(nullable = false)
    private int temporada;

    @Column(nullable = false)
    private int episodio;

    @Column(nullable = false)
    private double tempo;

    @Column(nullable = false)
    private double duracao;

    @Column(nullable = false)
    private boolean concluido;

    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Referência temporária para compatibilidade com a reprodução antiga
    @Transient
    private Producao producao;

    public Historico() {
    }

    // Construtor utilizado pelo catálogo e pelo histórico persistente
    public Historico(Long tmdbId, String tipo, String titulo,
                     String posterPath, double duracao) {

        this.tmdbId = tmdbId;
        this.tipo = tipo;
        this.titulo = titulo;
        this.posterPath = posterPath;
        this.duracao = duracao;

        this.temporada = 0;
        this.episodio = 0;
        this.tempo = 0;
        this.concluido = false;
    }

    // Construtor para compatibilidade com ReproducaoService
    public Historico(Producao producao, int temporada, double tempo) {

        this.producao = producao;
        this.titulo = producao.getTitulo();
        this.tipo = producao instanceof Serie ? "tv" : "movie";
        this.posterPath = null;
        this.tmdbId = null;
        this.duracao = 0;

        this.temporada = temporada;
        this.episodio = 0;
        this.tempo = tempo;
        this.concluido = false;
    }

    // Atualizar progresso
    public void atualizarProgresso(double tempo, int temporada) {

        if (tempo < 0 || temporada < 0) {
            throw new IllegalArgumentException(
                "Tempo e temporada não podem ser negativos."
            );
        }

        this.tempo = tempo;
        this.temporada = temporada;

        if (duracao > 0 && tempo >= duracao) {
            marcarComoAssistido();
        } else {
            this.concluido = false;
        }

        this.dataAtualizacao = LocalDateTime.now();
    }

    // Atualizar progresso incluindo episódio
    public void atualizarProgresso(double tempo, int temporada, int episodio) {

        if (episodio < 0) {
            throw new IllegalArgumentException(
                "O episódio não pode ser negativo."
            );
        }

        atualizarProgresso(tempo, temporada);
        this.episodio = episodio;
    }

    // Verificar se o conteúdo foi assistido
    public boolean verificarConteudoAssistido() {
        return concluido;
    }

    // Marcar conteúdo como concluído
    public void marcarComoAssistido() {

        this.concluido = true;

        if (duracao > 0) {
            this.tempo = duracao;
        }

        this.dataAtualizacao = LocalDateTime.now();
    }

    // Consultar progresso salvo
    public double consultarProgresso() {
        return tempo;
    }

    // Calcular porcentagem assistida
    public double calcularPorcentagem() {

        if (duracao <= 0) {
            return 0;
        }

        double porcentagem = (tempo / duracao) * 100;

        return Math.min(porcentagem, 100);
    }

    @PrePersist
    public void prePersist() {

        if (dataAtualizacao == null) {
            dataAtualizacao = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    // Getters e setters

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

    public int getTemporada() {
        return temporada;
    }

    public void setTemporada(int temporada) {
        this.temporada = temporada;
    }

    public int getEpisodio() {
        return episodio;
    }

    public void setEpisodio(int episodio) {
        this.episodio = episodio;
    }

    public double getTempo() {
        return tempo;
    }

    public void setTempo(double tempo) {
        this.tempo = tempo;
    }

    public double getDuracao() {
        return duracao;
    }

    public void setDuracao(double duracao) {
        this.duracao = duracao;
    }

    public boolean isConcluido() {
        return concluido;
    }

    public void setConcluido(boolean concluido) {
        this.concluido = concluido;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Producao getProducao() {
        return producao;
    }

    public void setProducao(Producao producao) {
        this.producao = producao;
    }
}
