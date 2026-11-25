
package movieplex.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;

@Service
public class TmdbService {

    private final RestClient restClient;
    private final String apiKey;
    private final String idioma;

    public TmdbService(
            @Value("${tmdb.api.url}") String apiUrl,
            @Value("${tmdb.api.key}") String apiKey,
            @Value("${movieplex.catalog.language:pt-BR}") String idioma) {

        this.restClient = RestClient.builder()
                .baseUrl(apiUrl)
                .build();

        this.apiKey = apiKey;
        this.idioma = idioma;
    }

    // Consulta geral à API do TMDB
    private JsonNode consultar(String endpoint) {

        return consultar(endpoint, null);
    }

    // Consulta com informações adicionais
    private JsonNode consultar(
            String endpoint,
            String informacoesAdicionais) {

        return restClient.get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder
                            .path(endpoint)
                            .queryParam("api_key", apiKey)
                            .queryParam("language", idioma)
                            .queryParam("region", "BR");

                    if (informacoesAdicionais != null) {
                        builder.queryParam(
                                "append_to_response",
                                informacoesAdicionais
                        );
                    }

                    return builder.build();
                })
                .retrieve()
                .body(JsonNode.class);
    }

    // FILMES E SÉRIES EM ALTA
    public JsonNode buscarEmAlta() {
        return consultar("/trending/all/week");
    }

    // FILMES POPULARES
    public JsonNode listarFilmesPopulares() {
        return consultar("/movie/popular");
    }

    // SÉRIES POPULARES
    public JsonNode listarSeriesPopulares() {
        return consultar("/tv/popular");
    }

    // LANÇAMENTOS DE FILMES
    public JsonNode listarLancamentosFilmes() {
        return consultar("/movie/upcoming");
    }

    // SÉRIES EM EXIBIÇÃO
    public JsonNode listarLancamentosSeries() {
        return consultar("/tv/on_the_air");
    }

    // PESQUISA DE FILMES
    public JsonNode pesquisarFilmes(String titulo) {
        return pesquisar("/search/movie", titulo);
    }

    // PESQUISA DE SÉRIES
    public JsonNode pesquisarSeries(String titulo) {
        return pesquisar("/search/tv", titulo);
    }

    // PESQUISA GERAL
    public JsonNode pesquisarTudo(String titulo) {
        return pesquisar("/search/multi", titulo);
    }

    // MÉTODO AUXILIAR DE PESQUISA
    private JsonNode pesquisar(
            String endpoint,
            String titulo) {

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(endpoint)
                        .queryParam("api_key", apiKey)
                        .queryParam("language", idioma)
                        .queryParam("query", titulo)
                        .queryParam("page", 1)
                        .build())
                .retrieve()
                .body(JsonNode.class);
    }

    // DETALHES DE FILMES
    public JsonNode buscarDetalhesFilme(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "ID do filme inválido."
            );
        }

        return consultar(
                "/movie/" + id,
                "credits,videos"
        );
    }

    // DETALHES DE SÉRIES
    public JsonNode buscarDetalhesSerie(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "ID da série inválido."
            );
        }

        return consultar(
                "/tv/" + id,
                "credits,videos"
        );
    }
}
