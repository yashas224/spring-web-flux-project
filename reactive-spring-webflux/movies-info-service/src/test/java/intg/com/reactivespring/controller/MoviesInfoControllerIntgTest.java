package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class MoviesInfoControllerIntgTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    MovieInfoRepository movieInfoRepository;


    @BeforeEach
    void setUp() {
        var movieinfos = List.of(new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        movieInfoRepository.saveAll(movieinfos).blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void addMovieInfo() {

        webTestClient.post()
                .uri(uriBuilder -> {
                    return uriBuilder.path("/v1/movieInfos").build();
                })
                .bodyValue(new MovieInfo(null, "Test Movie",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")))
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var body = movieInfoEntityExchangeResult.getResponseBody();
                    Assertions.assertNotNull(body);
                    Assertions.assertNotNull(body.getMovieInfoId());
                    Assertions.assertEquals("Test Movie", body.getName());
                });
    }


    @Test
    void getAllMovieInfo() {
        var moviesInfos = webTestClient.get()
                .uri("/v1/movieInfos")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(MovieInfo.class)
                .getResponseBody().log();

        StepVerifier.create(moviesInfos)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void testGetAllMovieInfo() {
        String movieId = "abc";
        var movieInfoMono = webTestClient.get()
                .uri(uriBuilder -> {
                    return uriBuilder.path("/v1/movieInfos/" + movieId).build();
                })
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    Assertions.assertNotNull(movieInfoEntityExchangeResult.getResponseBody());
                    Assertions.assertEquals("abc", movieInfoEntityExchangeResult.getResponseBody().getMovieInfoId());
                });

    }

    @Test
    void testGetAllMovieInfoJsonPathApproach() {
        String movieId = "abc";
        var movieInfoMono = webTestClient.get()
                .uri(uriBuilder -> {
                    return uriBuilder.path("/v1/movieInfos/" + movieId).build();
                })
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Dark Knight Rises");

    }

    @Test
    void update() {
        String movieId = "abc";
        var updatedMovieInfo = MovieInfo.builder().name("Test Update").cast(Arrays.asList("aaa")).year(2000).build();
        webTestClient.put()
                .uri("/v1/movieInfos/" + movieId)
                .bodyValue(updatedMovieInfo)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var movieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    Assertions.assertNotNull(movieInfo);
                    Assertions.assertEquals("Test Update", movieInfo.getName());
                    Assertions.assertEquals("abc", movieInfo.getMovieInfoId());

                });
    }

    @Test
    void delete() {
        String movieId = "abc";

        webTestClient.delete()
                .uri("/v1/movieInfos/" + movieId)
                .exchange()
                .expectStatus().isNoContent();

        webTestClient.get()
                .uri("/v1/movieInfos/" + movieId)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Void.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var movieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    System.out.println("OBJECT--" + movieInfo);
                    Assertions.assertNull(movieInfo);
                });
    }

    @Test
    void update_Notfound() {
        String movieId = "def";
        var updatedMovieInfo = MovieInfo.builder().name("Test Update").cast(Arrays.asList("aaa")).year(2000).build();
        webTestClient.put()
                .uri("/v1/movieInfos/" + movieId)
                .bodyValue(updatedMovieInfo)
                .exchange()
                .expectStatus().isNotFound()
        ;
    }

    @Test
    void getAllMovieInfoByYear() {
        var moviesInfos = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/v1/movieInfos")
                        .replaceQueryParam("year", 2012).
                        build())
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(MovieInfo.class)
                .getResponseBody().log();

        StepVerifier.create(moviesInfos)
                .expectNextCount(1)
                .verifyComplete();
    }

}