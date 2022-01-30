package com.reactivespring.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
public class FluxAndMonoController {

    @RequestMapping(method = RequestMethod.GET, value = "/flux")
    public Flux<Integer> getFlux() {
        return Flux.just(1, 2, 3, 4).log();
    }

    @GetMapping("/mono")
    public Mono<String> getMono() {
        return Mono.just("Hello-world").log();
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Long> stream() {
        return Flux.interval(Duration.ofSeconds(1)).log();
    }
}
