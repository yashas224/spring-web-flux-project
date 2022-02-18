package com.reactivespring.client;

import com.reactivespring.domain.MovieInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class MoviesInfoRestClient {


    @Autowired
    private WebClient webClient;

    @Value("${restClient.moviesInfoURL}")
    private String url;

    public Mono<MovieInfo> retriveMovieInfo(String movieId) {
        return webClient.get().uri(url + "/{id}", movieId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(MovieInfo.class)
                .log();
    }


}
