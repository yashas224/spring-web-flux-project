package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void namesFlux() {
        var flux = fluxAndMonoGeneratorService.namesFlux().log();
        StepVerifier.create(flux)
                .expectNext("alex", "ben", "chloe")
                .verifyComplete();
    }

    @Test
    void namesFlus_Map() {
        var flux = fluxAndMonoGeneratorService.namesFlus_Map().log();
        StepVerifier.create(flux)
                .expectNext("ALEX", "BEN", "CHLOE")
                .verifyComplete();
    }

    @Test
    void namesFlus_immutbility() {
        var flux = fluxAndMonoGeneratorService.namesFlus_immutbility().log();
        StepVerifier.create(flux)
                .expectNext("alex", "ben", "chloe")
                .verifyComplete();
    }

    @Test
    void namesFlus_filter() {
        var flux = fluxAndMonoGeneratorService.namesFlus_filter(3).log();
        StepVerifier.create(flux)
                .expectNext("4-ALEX", "5-CHLOE")
                .verifyComplete();
    }

    @Test
    void namesFlus_flatMap() {
        var flux = fluxAndMonoGeneratorService.namesFlus_flatMap(3).log();
        StepVerifier.create(flux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }


    @Test
    void namesFlus_flatMap_async() {
        var flux = fluxAndMonoGeneratorService.namesFlus_flatMap_async(3).log();
        StepVerifier.create(flux)
//                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void namesFlus_concat() {
        var flux = fluxAndMonoGeneratorService.namesFlus_concatMap(3).log();
        StepVerifier.create(flux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
//                .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void namesMono_flat_map() {
        var mono = fluxAndMonoGeneratorService.namesMono_flat_map().log();
        StepVerifier.create(mono)
                .expectNext(List.of("ALEX".split("")))
//                .expectNextCount(9)
                .verifyComplete();
    }


    @Test
    void namesMono_flat_map_many() {
        var flux = fluxAndMonoGeneratorService.namesMono_flat_map_many().log();
        StepVerifier.create(flux)
                .expectNext("A", "L", "E", "X")
                .verifyComplete();
    }

    @Test
    void namesFlus_transform() {
        var flux = fluxAndMonoGeneratorService.namesFlus_transform(3).log();
        StepVerifier.create(flux)
                .expectNext("ALEX", "CHLOE")
                .verifyComplete();

    }


    @Test
    void namesFlus_transform_1() {
        var flux = fluxAndMonoGeneratorService.namesFlus_transform(6).log();
        StepVerifier.create(flux)
                .expectNext("DEFAULT")
                .verifyComplete();

    }


    @Test
    void namesFlus_switch_if_empty() {
        var flux = fluxAndMonoGeneratorService.namesFlus_switch_if_empty(6).log();
        StepVerifier.create(flux)
                .expectNext("DEFAULT1", "DEFAULT@")
                .verifyComplete();
    }

    @Test
    void explore_concat() {
        var flux = fluxAndMonoGeneratorService.explore_concat().log();
        StepVerifier.create(flux)
                .expectNext("a", "b", "c", "d", "e", "f")
                .verifyComplete();
    }

    @Test
    void explore_merge() {
        var flux = fluxAndMonoGeneratorService.explore_merge();
        StepVerifier.create(flux)
                .expectNext("a", "d", "b", "e", "c", "f")
                .verifyComplete();
    }

    @Test
    void explore_merge_sequetial() {
        var flux = fluxAndMonoGeneratorService.explore_merge_sequetial();
        StepVerifier.create(flux)
                .expectNext("a", "b", "c", "d", "e", "f")
                .verifyComplete();
    }

    @Test
    void explore_zip() {
        var flux = fluxAndMonoGeneratorService.explore_zip().log();
        StepVerifier.create(flux)
                .expectNext("ad", "be", "cf")
                .verifyComplete();
    }

    @Test
    void explore_zip1() {
        var flux = fluxAndMonoGeneratorService.explore_zip1().log();
        StepVerifier.create(flux)
                .expectNext("ad14", "be25", "cf36")
                .verifyComplete();
    }
}