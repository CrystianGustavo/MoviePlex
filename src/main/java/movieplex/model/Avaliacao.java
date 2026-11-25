
package movieplex.model;

import java.time.LocalDate;

public class Avaliacao {

    private int id;
    private int nota;
    private String comentario;
    private LocalDate dataAvaliacao;

    private Usuario usuario;
    private Producao producao;

    public Avaliacao(int id, int nota, String comentario,
                     Usuario usuario, Producao producao) {

        this.id = id;
        this.nota = nota;
        this.comentario = comentario;
        this.usuario = usuario;
        this.producao = producao;
        this.dataAvaliacao = LocalDate.now();
    }

    public boolean validarNota() {
        return nota >= 1 && nota <= 5;
    }

    public boolean salvarAvaliacao() {

        if (!validarNota() || usuario == null || producao == null) {
            return false;
        }

        boolean assistiu = false;

        for (Historico historico : usuario.getHistoricos()) {

            if (historico.getTitulo() != null
                    && historico.getTitulo().equalsIgnoreCase(producao.getTitulo())
                    && historico.verificarConteudoAssistido()) {

                assistiu = true;
                break;
            }
        }

        if (!assistiu) {
            return false;
        }

        dataAvaliacao = LocalDate.now();
        usuario.adicionarAvaliacao(this);

        return true;
    }

    public boolean editarAvaliacao(int novaNota, String novoComentario) {

        if (novaNota < 1 || novaNota > 5) {
            return false;
        }

        this.nota = novaNota;
        this.comentario = novoComentario;
        this.dataAvaliacao = LocalDate.now();

        return true;
    }

    public int getId() {
        return id;
    }

    public int getNota() {
        return nota;
    }

    public String getComentario() {
        return comentario;
    }

    public LocalDate getDataAvaliacao() {
        return dataAvaliacao;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Producao getProducao() {
        return producao;
    }
}
