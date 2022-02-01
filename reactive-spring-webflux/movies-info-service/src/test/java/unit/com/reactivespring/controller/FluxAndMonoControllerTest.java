package com.reactivespring.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;


@WebFluxTest(FluxAndMonoController.class)
class FluxAndMonoControllerTest {

    @Autowired
    WebTestClient webTestClient;


    @Test
    void getFlux() {

        webTestClient
                .get()
                .uri("/flux")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Integer.class)
                .hasSize(4);
    }

    @Test
    void getFlux_approach2() {

        Flux<Integer> result = webTestClient
                .get()
                .uri("/flux")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(Integer.class)
                .getResponseBody();

        StepVerifier.create(result)
                .expectNext(1, 2, 3, 4)
                .verifyComplete();
    }

    @Test
    void getFlux_approach3() {

        webTestClient
                .get()
                .uri("/flux")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Integer.class)
                .consumeWith(listEntityExchangeResult -> {
                    var response = listEntityExchangeResult.getResponseBody();
                    Assertions.assertEquals(4, response.size());
                });
    }


    @Test
    void mono() {
        webTestClient
                .get()
                .uri("/mono")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(String.class)
                .consumeWith(listEntityExchangeResult -> {
                    var response = listEntityExchangeResult.getResponseBody();
                    Assertions.assertEquals("Hello-world", response);
                });
    }


    @Test
    void SSE() {

        Flux<Long> result = webTestClient
                .get()
                .uri("/stream")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier.create(result)
                .expectNext(0l, 1l, 2l)
                .thenCancel()
                .verify();
    }
}