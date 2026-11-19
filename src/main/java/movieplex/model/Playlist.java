package main.java.movieplex.model;

import java.util.ArrayList;
import java.util.List;

public class Playlist {

    private String nome;
    private List<Producao> producoes;

    public Playlist(String nome) {
        this.nome = nome;
        this.producoes = new ArrayList<>();
    }

    // Adicionar produção
    public boolean adicionar(Producao producao) {

        if (producao == null || producoes.contains(producao)) {
            return false;
        }

        producoes.add(producao);
        return true;
    }

    // Remover produção
    public boolean remover(Producao producao) {
        return producoes.remove(producao);
    }

    // Listar produções
    public List<Producao> listar() {
        return new ArrayList<>(producoes);
    }

    // Verificar se uma produção está na playlist
    public boolean contem(Producao producao) {
        return producoes.contains(producao);
    }

    // Getters
    public String getNome() {
        return nome;
    }

    public List<Producao> getProducoes() {
        return listar();
    }
}
