package movieplex.model;

public abstract class Producao {

    private String titulo;
    private String sinopse;
    private int classificacao;

    public Producao(String titulo, String sinopse, int classificacao) {
        this.titulo = titulo;
        this.sinopse = sinopse;
        this.classificacao = classificacao;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getSinopse() {
        return sinopse;
    }

    public int getClassificacao() {
        return classificacao;
    }

    public abstract void reproduzir();

    public abstract void pausar();
}
