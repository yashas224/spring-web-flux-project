package com.reactivespring.controller;

import com.reactivespring.client.MoviesInfoRestClient;
import com.reactivespring.client.ReviewsRestClient;
import com.reactivespring.domain.Movie;
import com.reactivespring.domain.MovieInfo;
import com.reactivespring.domain.Review;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/v1/movies")
@Slf4j
public class MoviesController {

    @Autowired
    private MoviesInfoRestClient moviesInfoRestClient;

    @Autowired
    private ReviewsRestClient reviewsRestClient;

    @GetMapping("/{id}")
    public Mono<Movie> getMovies(@PathVariable(name = "id") String movieId) {
        Mono<MovieInfo> movieInfoMono = moviesInfoRestClient.retriveMovieInfo(movieId);
        log.info("After retriving Movie Info Object");
        return movieInfoMono
                .flatMap(movieInfo -> {
                    Flux<Review> flux = reviewsRestClient.retriveRevew(movieId);
                    Mono<List<Review>> listMono = flux.collectList();
                    log.info("List is" + listMono);
                    return listMono.map(reviews -> new Movie(movieInfo, reviews));
                });
    }

}
