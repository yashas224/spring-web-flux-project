package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MoviesInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1")
public class MoviesInfoController {

    @Autowired
    private MoviesInfoService movieInfoService;

    @PostMapping("/movieInfos")
    @ResponseStatus(value = HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@Valid @RequestBody MovieInfo movieInfo) {
        return movieInfoService.addMovieInfo(movieInfo).log();
    }


    @GetMapping("/movieInfos")
    public Flux<MovieInfo> getAllMovieInfo() {
        return movieInfoService.getAllMovieInfos();
    }

    @GetMapping("/movieInfos/{movieInfoId}")
    public Mono<MovieInfo> getMovieInfoById(@PathVariable(name = "movieInfoId") String movieInfoId) {
        return movieInfoService.getMovieInfo(movieInfoId);
    }

    @PutMapping("/movieInfos/{id}")
    public Mono<ResponseEntity<MovieInfo>> update(@PathVariable(name = "id") String id, @Valid @RequestBody MovieInfo updatedMovieInfo) {
        return movieInfoService.updateMovieInfo(updatedMovieInfo, id)
                .map(movieInfo -> {
                    return ResponseEntity.ok().body(movieInfo);
                })
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .log();
    }


    @DeleteMapping("/movieInfos/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String id) {
        return movieInfoService.delete(id).log();
    }
}
