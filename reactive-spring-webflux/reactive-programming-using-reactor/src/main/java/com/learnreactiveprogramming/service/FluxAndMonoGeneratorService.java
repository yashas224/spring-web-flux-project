package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class FluxAndMonoGeneratorService {

    public Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"));
    }

    public Mono<String> nameMono() {
        return Mono.just("Alex");
    }

    public static void main(String[] args) {
        FluxAndMonoGeneratorService obj = new FluxAndMonoGeneratorService();
        Flux<String> flux = obj.namesFlux().log();

//        flux.subscribe(System.out::println);
        obj.nameMono().log().subscribe(System.out::println);
    }
}
