package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import com.reactivespring.service.MoviesInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1")
public class MoviesInfoController {

    @Autowired
    private MoviesInfoService movieInfoService;

    @PostMapping("/movieInfos")
    @ResponseStatus(value = HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody MovieInfo movieInfo) {
        return movieInfoService.addMovieInfo(movieInfo).log();
    }


    @GetMapping("/movieInfos")
    public Flux<MovieInfo> getAllMovieInfo() {
        return movieInfoService.getAllMovieInfos();
    }

    @GetMapping("/movieInfos/{movieInfoId}")
    public Mono<MovieInfo> getAllMovieInfo(@PathVariable(name = "movieInfoId") String movieInfoId) {
        return movieInfoService.getMovieInfo(movieInfoId);
    }

    @PutMapping("/movieInfos/{id}")
    public Mono<MovieInfo> update(@PathVariable(name = "id") String id, @RequestBody MovieInfo updatedMovieInfo) {
        return movieInfoService.updateMovieInfo(updatedMovieInfo, id);
    }


    @DeleteMapping("/movieInfos/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String id) {
        return movieInfoService.delete(id);
    }
}
