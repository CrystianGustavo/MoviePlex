
package movieplex.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "assinaturas")
public class Assinatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String plano;

    @Column(nullable = false)
    private double valor;

    @Column(nullable = false)
    private LocalDate dataInicio;

    @Column(nullable = false)
    private LocalDate dataFim;

    @Column(nullable = false)
    private boolean ativo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    // Construtor obrigatório para o JPA
    public Assinatura() {
    }

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
                && dataInicio != null
                && dataFim != null
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

    public void reativarAssinatura(
        String novoPlano,
        double novoValor,
        LocalDate novaDataInicio,
        LocalDate novaDataFim) {

            this.plano = novoPlano;
            this.valor = novoValor;
            this.dataInicio = novaDataInicio;
            this.dataFim = novaDataFim;
            this.ativo = true;
        }

    // Getters e setters

    public Long getId() {
        return id;
    }

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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
