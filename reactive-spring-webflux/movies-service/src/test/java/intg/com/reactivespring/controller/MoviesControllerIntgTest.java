package com.reactivespring.controller;


import com.reactivespring.domain.Movie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWireMock(port = 8084)
@TestPropertySource(
        properties = {"restClient.moviesInfoURL=http://localhost:8084/v1/movieInfos",
                "restClient.reviewsURL=http://localhost:8084/v1/reviews"}
)
public class MoviesControllerIntgTest {

    @Value("${restClient.moviesInfoURL")
    private String mivoisInfoUrl;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void retriveMovieById() {
        String movieId = "movieId";

        // mock for Movie Info Service Call
        stubFor(get(urlEqualTo("/v1/movieInfos/" + movieId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("movieinfo.json")));

        // mock for reviews Service Call
        stubFor(get(urlPathEqualTo("/v1/reviews")).withQueryParam("movieInfoId", equalTo(movieId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("reviews.json")));


        webTestClient.get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Movie.class)
                .consumeWith(movieEntityExchangeResult -> {
                    var result = movieEntityExchangeResult.getResponseBody();
                    Assertions.assertNotNull(result);
                    Assertions.assertNotNull(result.getMovieInfo());
                    Assertions.assertNotNull(result.getReviewList());
                    Assertions.assertEquals("Batman Begins", result.getMovieInfo().getName());
                    Assertions.assertEquals(2, result.getReviewList().size());
                });
    }


    @Test
    void retriveMovieById_404_movieInfoCall() {
        String movieId = "movieId";

        // mock for Movie Info Service Call
        stubFor(get(urlEqualTo("/v1/movieInfos/" + movieId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value())
                ));

        webTestClient.get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody(String.class)
                .consumeWith(movieEntityExchangeResult -> {
                    var result = movieEntityExchangeResult.getResponseBody();
                    Assertions.assertEquals("There is no Movie Info for id" + movieId, result);
                });
        verify(1, getRequestedFor(urlEqualTo("/v1/movieInfos/" + movieId)));

    }


    @Test
    void retriveMovieById_404_reviewsCall() {
        {
            String movieId = "movieId";

            // mock for Movie Info Service Call
            stubFor(get(urlEqualTo("/v1/movieInfos/" + movieId))
                    .willReturn(aResponse()
                            .withStatus(HttpStatus.OK.value())
                            .withHeader("Content-Type", "application/json")
                            .withBodyFile("movieinfo.json")));

            // mock for reviews Service Call
            stubFor(get(urlPathEqualTo("/v1/reviews")).withQueryParam("movieInfoId", equalTo(movieId))
                    .willReturn(aResponse()
                            .withStatus(HttpStatus.NOT_FOUND.value())
                    ));


            webTestClient.get()
                    .uri("/v1/movies/{id}", movieId)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody(Movie.class)
                    .consumeWith(movieEntityExchangeResult -> {
                        var result = movieEntityExchangeResult.getResponseBody();
                        Assertions.assertNotNull(result);
                        Assertions.assertNotNull(result.getMovieInfo());
                        Assertions.assertNotNull(result.getReviewList());
                        Assertions.assertEquals("Batman Begins", result.getMovieInfo().getName());
                        Assertions.assertEquals(0, result.getReviewList().size());
                    });
        }
    }

    @Test
    void retriveMovieById_500() {
        {
            String movieId = "movieId";

            // mock for Movie Info Service Call
            stubFor(get(urlEqualTo("/v1/movieInfos/" + movieId))
                    .willReturn(aResponse()
                            .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    ));

            webTestClient.get()
                    .uri("/v1/movies/{id}", movieId)
                    .exchange()
                    .expectStatus()
                    .is5xxServerError()
                    .expectBody(String.class)
                    .consumeWith(movieEntityExchangeResult -> {
                        var result = movieEntityExchangeResult.getResponseBody();
                        Assertions.assertEquals("Sorry Exception Occured !!!", result);
                    });

            verify(4, getRequestedFor(urlEqualTo("/v1/movieInfos/" + movieId)));
        }
    }


    @Test
    void retriveMovieById_Reviews_5xx() {
        String movieId = "movieId";

        // mock for Movie Info Service Call
        stubFor(get(urlEqualTo("/v1/movieInfos/" + movieId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("movieinfo.json")));

        // mock for reviews Service Call
        stubFor(get(urlPathEqualTo("/v1/reviews")).withQueryParam("movieInfoId", equalTo(movieId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .withHeader("Content-Type", "application/json")
                ));


        webTestClient.get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus()
                .is5xxServerError();

        verify(4, getRequestedFor(urlPathEqualTo("/v1/reviews")).withQueryParam("movieInfoId", equalTo(movieId)));

    }
}
