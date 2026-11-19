package main.java.movieplex.model;

public class Historico {

    private int temporada;
    private double tempo;
    private Producao producao;
    private boolean concluido;

    public Historico(Producao producao, int temporada, double tempo) {
        this.producao = producao;
        this.temporada = temporada;
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
    }

    // Verificar se o conteúdo foi assistido
    public boolean verificarConteudoAssistido() {
        return concluido;
    }

    // Marcar conteúdo como concluído
    public void marcarComoAssistido() {
        this.concluido = true;
    }

    // Retornar progresso salvo
    public double consultarProgresso() {
        return tempo;
    }

    // Getters
    public int getTemporada() {
        return temporada;
    }

    public double getTempo() {
        return tempo;
    }

    public Producao getProducao() {
        return producao;
    }

    public boolean isConcluido() {
        return concluido;
    }
}
