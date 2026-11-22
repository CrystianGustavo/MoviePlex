
package main.java.movieplex.service;

import movieplex.model.Usuario;
import movieplex.model.Assinatura;
import movieplex.model.Producao;
import movieplex.model.Historico;

import java.util.HashMap;
import java.util.Map;

public class ReproducaoService {

    private Map<Historico, Boolean> reproducoes;
    private int volume;

    public ReproducaoService() {
        this.reproducoes = new HashMap<>();
        this.volume = 50;
    }

    // Verificar se o usuário possui assinatura ativa
    private boolean possuiAssinaturaAtiva(Usuario usuario) {

        for (Assinatura assinatura : usuario.getAssinaturas()) {
            if (assinatura.validarAssinatura()) {
                return true;
            }
        }

        return false;
    }

    // R8 - Assistir filme ou série
    public Historico assistir(Usuario usuario, Producao producao) {

        if (usuario == null || producao == null
                || !possuiAssinaturaAtiva(usuario)) {
            return null;
        }

        Historico historico = new Historico(producao, 0, 0);

        usuario.adicionarHistorico(historico);
        producoes.put(historico, true);

        producao.reproduzir();

        return historico;
    }

    // R9 - Pausar reprodução
    public boolean pausar(Historico historico) {

        if (historico == null || !Boolean.TRUE.equals(
                reproducoes.get(historico))) {
            return false;
        }

        reproducoes.put(historico, false);

        return true;
    }

    // R10 - Ajustar volume
    public boolean ajustarVolume(int novoVolume) {

        if (novoVolume < 0 || novoVolume > 100) {
            return false;
        }

        this.volume = novoVolume;

        return true;
    }

    // R11 - Parar reprodução
    public boolean parar(Historico historico) {

        if (historico == null
                || !reproducoes.containsKey(historico)) {
            return false;
        }

        reproducoes.put(historico, false);

        return true;
    }

    // R12 - Retomar reprodução
    public boolean retomar(Usuario usuario, Historico historico) {

        if (usuario == null || historico == null
                || !possuiAssinaturaAtiva(usuario)) {
            return false;
        }

        if (!usuario.getHistoricos().contains(historico)) {
            return false;
        }

        if (historico.verificarConteudoAssistido()) {
            return false;
        }

        if (historico.consultarProgresso() <= 0) {
            return false;
        }

        reproducoes.put(historico, true);

        historico.getProducao().reproduzir();

        System.out.println(
                "Retomando em: " + historico.consultarProgresso()
        );

        return true;
    }

    // Atualizar progresso
    public boolean atualizarProgresso(Historico historico,
                                       double tempo,
                                       int temporada) {

        if (historico == null
                || !Boolean.TRUE.equals(reproducoes.get(historico))) {
            return false;
        }

        historico.atualizarProgresso(tempo, temporada);

        return true;
    }

    public int getVolume() {
        return volume;
    }

    public boolean estaReproduzindo(Historico historico) {
        return Boolean.TRUE.equals(reproducoes.get(historico));
    }
}
