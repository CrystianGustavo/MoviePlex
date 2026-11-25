package movieplex.service;

import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class VideoService {

    private final Map<Long, String> trailers = Map.of(
        1368337L, "/videos/the-odyssey.mp4",
        1726L, "/videos/iron-man.mp4",
        969681L, "/videos/spider-man.mp4"
    );

    public String buscarTrailer(Long tmdbId) {
        return trailers.get(tmdbId);
    }

    public boolean possuiTrailer(Long tmdbId) {
        return trailers.containsKey(tmdbId);
    }
}
