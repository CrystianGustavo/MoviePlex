package main.java.movieplex.model;

import java.time.LocalDate;

public class Assinatura {

    private String plano;
    private double valor;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private boolean ativo;

    public Assinatura(String plano, double valor,
                      LocalDate dataInicio, LocalDate dataFim) {

        this.plano = plano;
        this.valor = valor;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.ativo = true;
    }

    // Validar assinatura
    public boolean validarAssinatura() {
        LocalDate hoje = LocalDate.now();

        return ativo
                && !hoje.isBefore(dataInicio)
                && !hoje.isAfter(dataFim);
    }

    // Renovar assinatura
    public void renovarAssinatura(LocalDate novaDataFim) {
        this.dataFim = novaDataFim;
        this.ativo = true;
    }

    // Alterar plano
    public void alterarPlano(String plano, double valor) {
        this.plano = plano;
        this.valor = valor;
    }

    // Cancelar assinatura
    public void cancelarAssinatura() {
        this.ativo = false;
    }

    // Getters
    public String getPlano() {
        return plano;
    }

    public double getValor() {
        return valor;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public boolean isAtivo() {
        return ativo;
    }
}
