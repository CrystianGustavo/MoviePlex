
package movieplex.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String senha;

    // Serão mapeadas para o banco em etapas posteriores.
    @OneToOne(
        mappedBy = "usuario",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private Assinatura assinatura;

    @OneToMany(
    mappedBy = "usuario",
    cascade = CascadeType.ALL,
    orphanRemoval = true
)
private List<ConteudoSalvo> conteudosSalvos = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Historico> historicos = new ArrayList<>();

    @Transient
    private List<Playlist> playlists = new ArrayList<>();

    @Transient
    private List<Avaliacao> avaliacoes = new ArrayList<>();

    // Construtor obrigatório para o JPA
    public Usuario() {
    }

    public Usuario(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    // Atualizar perfil
    public void atualizarPerfil(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    // Adicionar assinatura
    public void adicionarAssinatura(Assinatura assinatura) {
        this.assinatura = assinatura;

        if (assinatura != null) {
            assinatura.setUsuario(this);
        }
    }

    // Criar playlist
    public Playlist criarPlaylist(String nome) {
        Playlist playlist = new Playlist(nome);
        playlists.add(playlist);
        return playlist;
    }

    // Registrar histórico
    public void adicionarHistorico(Historico historico) {

        if (historico == null) {
            return;
        }

        historico.setUsuario(this);
        historicos.add(historico);
    }

    public boolean removerHistorico(Long id) {

        return historicos.removeIf(
            historico -> historico.getId() != null
                && historico.getId().equals(id)
        );
    }

    // Registrar avaliação
    public void adicionarAvaliacao(Avaliacao avaliacao) {
        avaliacoes.add(avaliacao);
    }

    // Adicionar conteúdo à Minha Lista
    public void adicionarConteudoSalvo(ConteudoSalvo conteudo) {

        if (conteudo == null) {
            return;
        }

        boolean jaExiste = conteudosSalvos.stream().anyMatch(item ->
            item.getTmdbId().equals(conteudo.getTmdbId())
            && item.getTipo().equals(conteudo.getTipo())
        );

        if (!jaExiste) {
            conteudo.setUsuario(this);
            conteudosSalvos.add(conteudo);
        }
    }

    // Remover conteúdo da Minha Lista
    public boolean removerConteudoSalvo(Long id) {

        return conteudosSalvos.removeIf(
            conteudo -> conteudo.getId().equals(id)
        );
    }

    // Listar conteúdos salvos
    public List<ConteudoSalvo> getConteudosSalvos() {
        return conteudosSalvos;
    }

    // Getters e setters

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Assinatura getAssinatura() {
        return assinatura;
    }

    public List<Historico> getHistoricos() {
        return historicos;
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public List<Avaliacao> getAvaliacoes() {
        return avaliacoes;
    }
}
