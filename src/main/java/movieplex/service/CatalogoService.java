package movieplex.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.annotation.PostConstruct;
import movieplex.model.ConteudoCatalogo;

@Service
public class CatalogoService {

    private final TmdbService tmdbService;

    @Value("${movieplex.catalog.language:pt-BR}")
    private String idioma;

    private volatile List<ConteudoCatalogo> emAlta = new ArrayList<>();
    private volatile List<ConteudoCatalogo> filmesPopulares = new ArrayList<>();
    private volatile List<ConteudoCatalogo> seriesPopulares = new ArrayList<>();
    private volatile List<ConteudoCatalogo> lancamentosFilmes = new ArrayList<>();
    private volatile List<ConteudoCatalogo> lancamentosSeries = new ArrayList<>();

    public CatalogoService(TmdbService tmdbService) {
        this.tmdbService = tmdbService;
    }

    // Carrega o catálogo quando a aplicação inicia
    @PostConstruct
    public void carregarCatalogoInicial() {
        atualizarCatalogo();
    }

    // Atualiza o catálogo a cada 12 horas
    @Scheduled(fixedDelayString = "${movieplex.catalog.refresh:43200000}")
    public void atualizarCatalogo() {

        System.out.println("Atualizando catálogo do MoviePlex...");

        emAlta = consultarSecao(
                "Em alta",
                () -> tmdbService.buscarEmAlta(),
                10,
                true,
                null
        );

        filmesPopulares = consultarSecao(
                "Filmes populares",
                () -> tmdbService.listarFilmesPopulares(),
                20,
                false,
                "movie"
        );

        seriesPopulares = consultarSecao(
                "Séries populares",
                () -> tmdbService.listarSeriesPopulares(),
                20,
                false,
                "tv"
        );

        lancamentosFilmes = consultarSecao(
                "Lançamentos de filmes",
                () -> tmdbService.listarLancamentosFilmes(),
                10,
                false,
                "movie"
        );

        lancamentosSeries = consultarSecao(
                "Lançamentos de séries",
                () -> tmdbService.listarLancamentosSeries(),
                10,
                false,
                "tv"
        );

        System.out.println("Catálogo do MoviePlex atualizado!");
    }

    private List<ConteudoCatalogo> consultarSecao(
            String nome,
            ConsultaTmdb consulta,
            int limite,
            boolean verificarTipo,
            String tipoPadrao) {

        try {
            JsonNode resposta = consulta.executar();

            JsonNode resultados = resposta.path("results");

            List<ConteudoCatalogo> conteudos = new ArrayList<>();

            for (JsonNode item : resultados) {

                String tipo = tipoPadrao;

                if (verificarTipo) {
                    tipo = item.path("media_type").asText("");

                    if (!tipo.equals("movie") && !tipo.equals("tv")) {
                        continue;
                    }
                }

                if (conteudos.size() >= limite) {
                    break;
                }

                ConteudoCatalogo conteudo = converter(item, tipo);

                if (conteudo != null) {
                    conteudos.add(conteudo);
                }
            }

            System.out.println(nome + ": " + conteudos.size() + " conteúdos.");

            return List.copyOf(conteudos);

        } catch (RestClientException e) {

            System.err.println(
                    "Não foi possível atualizar " + nome + ": " + e.getMessage()
            );

            return List.of();
        }
    }

    public ConteudoCatalogo converter(JsonNode item, String tipo) {

        int id = item.path("id").asInt();

        String titulo = tipo.equals("movie")
                ? item.path("title").asText("")
                : item.path("name").asText("");

        if (titulo.isBlank()) {
            return null;
        }

        String sinopse = item.path("overview").asText(
                "Sinopse indisponível."
        );

        String posterUrl = montarUrlImagem(
                item.path("poster_path").asText(null),
                "w500"
        );

        String backdropUrl = montarUrlImagem(
                item.path("backdrop_path").asText(null),
                "w1280"
        );

        String dataLancamento = tipo.equals("movie")
                ? item.path("release_date").asText("")
                : item.path("first_air_date").asText("");

        double nota = item.path("vote_average").asDouble(0);

        return new ConteudoCatalogo(
                id,
                titulo,
                sinopse,
                tipo,
                posterUrl,
                backdropUrl,
                dataLancamento,
                nota
        );
    }

    private String montarUrlImagem(String caminho, String tamanho) {

        if (caminho == null || caminho.isBlank()) {
            return null;
        }

        return "https://image.tmdb.org/t/p/"
                + tamanho + caminho;
    }

    public List<ConteudoCatalogo> converterResultadosPesquisa(JsonNode resposta) {

    List<ConteudoCatalogo> resultados = new ArrayList<>();

    JsonNode conteudos = resposta.path("results");

        for (JsonNode item : conteudos) {

            String tipo = item.path("media_type").asText("");

            // Ignora pessoas e outros tipos de resultado
            if (!tipo.equals("movie") && !tipo.equals("tv")) {
                continue;
            }

            ConteudoCatalogo conteudo = converter(item, tipo);

            if (conteudo != null) {
                resultados.add(conteudo);
            }
        }

        return resultados;
    }

    public List<ConteudoCatalogo> getEmAlta() {
        return emAlta;
    }

    public List<ConteudoCatalogo> getFilmesPopulares() {
        return filmesPopulares;
    }

    public List<ConteudoCatalogo> getSeriesPopulares() {
        return seriesPopulares;
    }

    public List<ConteudoCatalogo> getLancamentosFilmes() {
        return lancamentosFilmes;
    }

    public List<ConteudoCatalogo> getLancamentosSeries() {
        return lancamentosSeries;
    }

    @FunctionalInterface
    private interface ConsultaTmdb {
        JsonNode executar();
    }
}
