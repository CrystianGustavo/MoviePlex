package movieplex.model;

public class ConteudoCatalogo {

    private final int id;
    private final String titulo;
    private final String sinopse;
    private final String tipo;
    private final String posterUrl;
    private final String backdropUrl;
    private final String dataLancamento;
    private final double nota;

    public ConteudoCatalogo(
            int id,
            String titulo,
            String sinopse,
            String tipo,
            String posterUrl,
            String backdropUrl,
            String dataLancamento,
            double nota) {

        this.id = id;
        this.titulo = titulo;
        this.sinopse = sinopse;
        this.tipo = tipo;
        this.posterUrl = posterUrl;
        this.backdropUrl = backdropUrl;
        this.dataLancamento = dataLancamento;
        this.nota = nota;
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getSinopse() {
        return sinopse;
    }

    public String getTipo() {
        return tipo;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getBackdropUrl() {
        return backdropUrl;
    }

    public String getDataLancamento() {
        return dataLancamento;
    }

    public double getNota() {
        return nota;
    }
}
