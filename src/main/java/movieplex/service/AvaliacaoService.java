
package main.java.movieplex.service;

import movieplex.model.Avaliacao;
import movieplex.model.Usuario;
import movieplex.model.Producao;

import java.util.ArrayList;
import java.util.List;

public class AvaliacaoService {

    private List<Avaliacao> avaliacoes;

    public AvaliacaoService() {
        this.avaliacoes = new ArrayList<>();
    }

    // Registrar avaliação
    public boolean avaliar(int id, int nota, String comentario,
                           Usuario usuario, Producao producao) {

        if (usuario == null || producao == null) {
            return false;
        }

        // Verificar se já existe avaliação
        for (Avaliacao existente : avaliacoes) {

            if (existente.getUsuario() == usuario
                    && existente.getProducao() == producao) {
                return false;
            }
        }

        Avaliacao avaliacao = new Avaliacao(
                id, nota, comentario, usuario, producao
        );

        if (!avaliacao.salvarAvaliacao()) {
            return false;
        }

        avaliacoes.add(avaliacao);

        return true;
    }

    // Editar avaliação existente
    public boolean editarAvaliacao(int id, int novaNota,
                                   String novoComentario,
                                   Usuario usuario) {

        if (usuario == null) {
            return false;
        }

        for (Avaliacao avaliacao : avaliacoes) {

            if (avaliacao.getId() == id
                    && avaliacao.getUsuario() == usuario) {

                return avaliacao.editarAvaliacao(
                        novaNota, novoComentario
                );
            }
        }

        return false;
    }

    // Consultar avaliações
    public List<Avaliacao> listarAvaliacoes() {
        return new ArrayList<>(avaliacoes);
    }

    // Buscar avaliação por usuário e produção
    public Avaliacao buscarAvaliacao(Usuario usuario,
                                     Producao producao) {

        for (Avaliacao avaliacao : avaliacoes) {

            if (avaliacao.getUsuario() == usuario
                    && avaliacao.getProducao() == producao) {
                return avaliacao;
            }
        }

        return null;
    }
}
