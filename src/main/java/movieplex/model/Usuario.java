package main.java.movieplex.model;

import java.util.ArrayList;
import java.util.List;

public class Usuario {

    private String nome;
    private String email;
    private String senha;

    private List<Assinatura> assinaturas;
    private List<Historico> historicos;
    private List<Playlist> playlists;
    private List<Avaliacao> avaliacoes;

    public Usuario(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;

        this.assinaturas = new ArrayList<>();
        this.historicos = new ArrayList<>();
        this.playlists = new ArrayList<>();
        this.avaliacoes = new ArrayList<>();
    }

    // Realizar login
    public boolean realizarLogin(String email, String senha) {
        return this.email.equals(email)
                && this.senha.equals(senha);
    }

    // Atualizar perfil
    public void atualizarPerfil(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    // Adicionar assinatura
    public void adicionarAssinatura(Assinatura assinatura) {
        assinaturas.add(assinatura);
    }

    // Criar playlist
    public Playlist criarPlaylist(String nome) {
        Playlist playlist = new Playlist(nome);
        playlists.add(playlist);
        return playlist;
    }

    // Registrar histórico
    public void adicionarHistorico(Historico historico) {
        historicos.add(historico);
    }

    // Registrar avaliação
    public void adicionarAvaliacao(Avaliacao avaliacao) {
        avaliacoes.add(avaliacao);
    }

    // Getters
    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public List<Assinatura> getAssinaturas() {
        return assinaturas;
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
