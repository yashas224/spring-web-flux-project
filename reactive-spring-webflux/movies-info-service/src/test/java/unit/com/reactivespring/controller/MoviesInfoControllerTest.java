package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MoviesInfoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebFluxTest(controllers = MoviesInfoController.class)
public class MoviesInfoControllerTest {

    @MockBean
    private MoviesInfoService movieInfoService;

    @Autowired
    WebTestClient webTestClient;

    @Test
    void getAllMoviesInfo() {
        when(movieInfoService.getAllMovieInfos()).thenReturn(Flux.just(MovieInfo.builder().name("test movie").build()));

        var movieInfos = webTestClient.get()
                .uri("/v1/movieInfos").exchange()
                .expectStatus()
                .isOk()
                .returnResult(MovieInfo.class)
                .getResponseBody();

        StepVerifier.create(movieInfos)
//                .expectNextCount(1)
                .assertNext(movieInfo -> {
                    Assertions.assertEquals("test movie", movieInfo.getName());
                })
                .verifyComplete();

        verify(movieInfoService, Mockito.times(1)).getAllMovieInfos();
    }

    @Test
    public void getMovieInfoById() {
        String movieinfoId = "movieinfoId";
        when(movieInfoService.getMovieInfo(movieinfoId)).
                thenReturn(Mono.just(MovieInfo.builder().name("test movie 1").build()));

        webTestClient.get()
                .uri("/v1/movieInfos/" + movieinfoId)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var movieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    Assertions.assertNotNull(movieInfo);
                    Assertions.assertEquals("test movie 1", movieInfo.getName());
                });

        verify(movieInfoService).getMovieInfo(movieinfoId);
    }


    @Test
    public void addMovieInfo() {
        MovieInfo movieInfoObj = MovieInfo.builder().name("test movie 1").movieInfoId("mockID").year(2021).build();

        when(movieInfoService.addMovieInfo(any(MovieInfo.class))).thenReturn(Mono.just(movieInfoObj));

        webTestClient.post()
                .uri("/v1/movieInfos")
                .bodyValue(movieInfoObj)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedObj = movieInfoEntityExchangeResult.getResponseBody();
                    Assertions.assertNotNull(savedObj);
                    Assertions.assertEquals("mockID", savedObj.getMovieInfoId());
                });

        verify(movieInfoService).addMovieInfo(any(MovieInfo.class));
    }


    @Test
    void update() {
        String movieinfoId = "movieinfoId";

        MovieInfo movieInfoObj = MovieInfo.builder().name("updated test movie 1").movieInfoId("mockID").year(2000).build();

        when(movieInfoService.updateMovieInfo(any(MovieInfo.class), Mockito.anyString())).thenReturn(Mono.just(movieInfoObj));

        webTestClient.put()
                .uri("/v1/movieInfos/" + movieinfoId)
                .bodyValue(movieInfoObj)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var updatedObj = movieInfoEntityExchangeResult.getResponseBody();
                    Assertions.assertNotNull(updatedObj);
                    Assertions.assertEquals("updated test movie 1", updatedObj.getName());
                });

        verify(movieInfoService).updateMovieInfo(any(MovieInfo.class), Mockito.anyString());
    }


    @Test
    public void addMovieInfo_validation() {
        MovieInfo movieInfoObj = MovieInfo.builder().name("").movieInfoId("mockID").year(2021).build();

        webTestClient.post()
                .uri("/v1/movieInfos")
                .bodyValue(movieInfoObj)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var responseStr = movieInfoEntityExchangeResult.getResponseBody();
                    Assertions.assertEquals("need minium 1 caste,MoieInfo Name must be present !!!",responseStr);

                });

        verifyNoInteractions(movieInfoService);
    }


}
