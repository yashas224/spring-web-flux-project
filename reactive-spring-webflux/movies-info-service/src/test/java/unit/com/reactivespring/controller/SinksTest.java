package com.reactivespring.controller;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import static reactor.core.publisher.Sinks.EmitFailureHandler.FAIL_FAST;

public class SinksTest {

    @Test
    void sink() {
        Sinks.Many<Integer> replaySink = Sinks.many().replay().all();

        replaySink.emitNext(1, FAIL_FAST);
        replaySink.emitNext(7, FAIL_FAST);

        Flux<Integer> fluxView = replaySink.asFlux();
        fluxView.subscribe(val -> System.out.println("sub1- " + val));

        Flux<Integer> fluxView1 = replaySink.asFlux();
        fluxView1.subscribe(val -> System.out.println("sub2- " + val));

        replaySink.tryEmitNext(99);

        fluxView1.subscribe(val -> System.out.println("sub3- " + val));
    }


    @Test
    void sinks_multicaste() {

        Sinks.Many<Integer> sink = Sinks.many().multicast().onBackpressureBuffer();
        Flux<Integer> fluxView = sink.asFlux();

        sink.tryEmitNext(3);
        sink.tryEmitNext(7);
        fluxView.subscribe(val -> System.out.println("sub1- " + val));

        fluxView.subscribe(val -> System.out.println("sub2- " + val));
        sink.tryEmitNext(9);
    }


    @Test
    void sinks_unicast() {

        Sinks.Many<Integer> sink = Sinks.many().unicast().onBackpressureBuffer();
        Flux<Integer> fluxView = sink.asFlux();

        sink.tryEmitNext(3);
        sink.tryEmitNext(7);
        fluxView.subscribe(val -> System.out.println("sub1- " + val));

        // exception thrown as Unicast allows only one subscriber
        fluxView.subscribe(val -> System.out.println("sub2- " + val));
        sink.tryEmitNext(9);
    }
}
