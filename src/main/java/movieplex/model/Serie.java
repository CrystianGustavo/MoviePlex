package main.java.movieplex.model;

public class Serie extends Producao {

    private int temporadas;
    private int episodios;

    public Serie(String titulo, String sinopse,
                 int classificacao, int temporadas,
                 int episodios) {

        super(titulo, sinopse, classificacao);
        this.temporadas = temporadas;
        this.episodios = episodios;
    }

    public int getTemporadas() {
        return temporadas;
    }

    public int getEpisodios() {
        return episodios;
    }

    @Override
    public void reproduzir() {
        System.out.println("Reproduzindo série: " + getTitulo());
    }

    @Override
    public void pausar() {
        System.out.println("Série pausada: " + getTitulo());
    }
}
