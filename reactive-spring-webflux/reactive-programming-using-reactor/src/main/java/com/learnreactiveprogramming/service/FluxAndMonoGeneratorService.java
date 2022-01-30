package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.Function;

public class FluxAndMonoGeneratorService {
    private static List<String> namesList = List.of("alex", "ben", "chloe");

    public Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"));
    }

    public Mono<String> nameMono() {
        return Mono.just("Alex");
    }

    public Flux<String> namesFlus_Map() {
        return Flux.fromIterable(namesList)
                .map(s -> s.toUpperCase(Locale.ROOT));
    }

    public Flux<String> namesFlus_immutbility() {
        var flux = Flux.fromIterable(namesList);
        flux.map(String::toUpperCase);
        return flux;
    }

    public Flux<String> namesFlus_filter(int length) {
        return Flux.fromIterable(namesList)
                .filter(s -> s.length() > length)
                .map(String::toUpperCase)
                .map(s -> s.length() + "-" + s);
    }

    public Flux<String> namesFlus_flatMap(int length) {
        return Flux.fromIterable(namesList)
                .filter(s -> s.length() > length)
                .map(String::toUpperCase)
                .flatMap(this::getSplitFlux);
    }

    public Flux<String> namesFlus_flatMap_async(int length) {
        return Flux.fromIterable(namesList)
                .filter(s -> s.length() > length)
                .map(String::toUpperCase)
                .flatMap(this::getSplitFluxDelay);
    }

    public Flux<String> namesFlus_concatMap(int length) {
        return Flux.fromIterable(namesList)
                .filter(s -> s.length() > length)
                .map(String::toUpperCase)
                .concatMap(this::getSplitFluxDelay);
    }

    public Flux<String> getSplitFlux(String s) {
        return Flux.fromArray(s.split(""));
    }

    public Flux<String> getSplitFluxDelay(String s) {
        return Flux.fromArray(s.split("")).delayElements(Duration.ofMillis(new Random().nextInt(1000)));
    }


    public Mono<List<String>> namesMono_flat_map() {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .flatMap(s -> splitstringMono(s));

    }

    public Flux<String> namesMono_flat_map_many() {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .flatMapMany(n -> getSplitFluxDelay(n));

    }

    private Mono<List<String>> splitstringMono(String s) {
        return Mono.just(List.of(s.split("")));
    }


    public Flux<String> namesFlus_transform(int length) {

        Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
                .filter(s -> s.length() > length);
        return Flux.fromIterable(namesList)
                .transform(filterMap)
                .defaultIfEmpty("DEFAULT");
    }

    public Flux<String> namesFlus_switch_if_empty(int length) {

        Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
                .filter(s -> s.length() > length);
        return Flux.fromIterable(namesList)
                .transform(filterMap)
                .switchIfEmpty(Flux.fromIterable(List.of("DEFAULT1", "DEFAULT@")));
    }

    public Flux<String> explore_concat() {
        var flux1 = Flux.just("a", "b", "c");
        var flux2 = Flux.just("d", "e", "f");

        return Flux.concat(flux1, flux2);
    }

    public Flux<String> explore_merge() {
        var flux1 = Flux.just("a", "b", "c").delayElements(Duration.ofMillis(100)).log();
        var flux2 = Flux.just("d", "e", "f").delayElements(Duration.ofMillis(125)).log();

        return Flux.merge(flux1, flux2);
    }

    public Flux<String> explore_merge_sequetial() {
        var flux1 = Flux.just("a", "b", "c").delayElements(Duration.ofMillis(100)).log();
        var flux2 = Flux.just("d", "e", "f").delayElements(Duration.ofMillis(125)).log();

        return Flux.mergeSequential(flux1, flux2);
    }

    public Flux<String> explore_zip() {
        var flux1 = Flux.just("a", "b", "c");
        var flux2 = Flux.just("d", "e", "f");


        return Flux.zip(flux1, flux2, (f1, f2) -> f1 + f2);
    }


    public Flux<String> explore_zip1() {
        var flux1 = Flux.just("a", "b", "c");
        var flux2 = Flux.just("d", "e", "f");
        var flux3 = Flux.just("1", "2", "3");
        var flux4 = Flux.just("4", "5", "6");

        return Flux.zip(flux1, flux2, flux3, flux4).map(tuple4 -> tuple4.getT1() + tuple4.getT2() + tuple4.getT3() + tuple4.getT4());
    }

    public static void main(String[] args) {
        FluxAndMonoGeneratorService obj = new FluxAndMonoGeneratorService();
        Flux<String> flux = obj.namesFlux().log();

//        flux.subscribe(System.out::println);
        obj.nameMono().log().subscribe(System.out::println);


        
    }
}
