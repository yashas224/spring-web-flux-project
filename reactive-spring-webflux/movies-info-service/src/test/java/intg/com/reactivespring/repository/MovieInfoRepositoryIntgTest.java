package com.reactivespring.repository;

import com.reactivespring.domain.MovieInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

@DataMongoTest
@ActiveProfiles("test")
class MovieInfoRepositoryIntgTest {


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
    void findAll() {
        Flux<MovieInfo> flux = movieInfoRepository.findAll().log();

        StepVerifier.create(flux)
                .expectNextCount(3)
                .verifyComplete();
    }


    @Test
    void findById() {
        Mono<MovieInfo> mono = movieInfoRepository.findById("abc").log();

        StepVerifier.create(mono)
                .assertNext(movieInfo -> {
                    Assertions.assertEquals("Dark Knight Rises", movieInfo.getName());
                })
                .verifyComplete();
    }

    @Test
    void saveMovieInfo() {
        MovieInfo movieInfo = MovieInfo.builder().
                name("Tadap").
                releaseDate(LocalDate.of(2022, 1, 1))
                .cast(List.of("Tara", "Ahan shetty"))
                .build();
        Mono<MovieInfo> mono = movieInfoRepository.save(movieInfo).log();

        StepVerifier.create(mono)
                .assertNext(movieInfo1 -> {
                    Assertions.assertEquals("Tadap", movieInfo1.getName());
                    Assertions.assertNotNull(movieInfo1.getMovieInfoId());
                    System.out.println("id is--" + movieInfo1.getMovieInfoId());
                })
                .verifyComplete();
    }

    @Test
    void updateMovieInfo() {
        MovieInfo movieInfo = movieInfoRepository.findById("abc").block();

        movieInfo.setName("Updated Movie");

        var mono = movieInfoRepository.save(movieInfo).log();

        StepVerifier.create(mono)
                .assertNext(movieInfo1 -> {
                    Assertions.assertEquals("abc", movieInfo1.getMovieInfoId());
                    Assertions.assertEquals("Updated Movie", movieInfo.getName());
                })
                .verifyComplete();
    }

    @Test
    void delete() {
        movieInfoRepository.deleteById("abc").block();

        Mono movieInfo = movieInfoRepository.findById("abc").log();

        StepVerifier.create(movieInfo)
                .verifyComplete();
    }
}