package main.java.movieplex.model;

public class Filme extends Producao {

    private double duracao;

    public Filme(String titulo, String sinopse,
                 int classificacao, double duracao) {

        super(titulo, sinopse, classificacao);
        this.duracao = duracao;
    }

    public double getDuracao() {
        return duracao;
    }

    @Override
    public void reproduzir() {
        System.out.println("Reproduzindo filme: " + getTitulo());
    }

    @Override
    public void pausar() {
        System.out.println("Filme pausado: " + getTitulo());
    }
}
