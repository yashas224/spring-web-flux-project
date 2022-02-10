package com.reactivespring.service;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MoviesInfoService {

    @Autowired
    MovieInfoRepository movieInfoRepository;

    public Mono<MovieInfo> addMovieInfo(MovieInfo movieInfo) {
        return movieInfoRepository.save(movieInfo);
    }

    public Flux<MovieInfo> getAllMovieInfos() {
        return movieInfoRepository.findAll();
    }

    public Mono<MovieInfo> getMovieInfo(String movieInfoId) {
        return movieInfoRepository.findById(movieInfoId);
    }

    public Mono<MovieInfo> updateMovieInfo(MovieInfo updatedMovieInfo, String id) {
        var oldbj = movieInfoRepository.findById(id);

        return oldbj.flatMap(movieInfo -> {
            movieInfo.setCast(updatedMovieInfo.getCast());
            movieInfo.setName(updatedMovieInfo.getName());
            movieInfo.setYear(updatedMovieInfo.getYear());
            movieInfo.setReleaseDate(updatedMovieInfo.getReleaseDate());
            return movieInfoRepository.save(movieInfo);
        });
    }

    public Mono<Void> delete(String id) {
        return movieInfoRepository.deleteById(id);
    }

    public Flux<MovieInfo> getByYear(int year) {
        return movieInfoRepository.findByYear(year);
    }
}
