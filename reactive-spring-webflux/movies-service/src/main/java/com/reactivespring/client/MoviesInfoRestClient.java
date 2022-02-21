package com.reactivespring.client;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.MoviesInfoServerException;
import com.reactivespring.util.RetryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.function.Predicate;

@Component
@Slf4j
public class MoviesInfoRestClient {


    @Autowired
    private WebClient webClient;

    @Value("${restClient.moviesInfoURL}")
    private String url;

    public Mono<MovieInfo> retriveMovieInfo(String movieId) {

        return webClient.get().uri(url + "/{id}", movieId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> {
                    if (clientResponse.statusCode() == HttpStatus.NOT_FOUND) {
                        return Mono.error(new MoviesInfoClientException("There is no Movie Info for id" + movieId, HttpStatus.NOT_FOUND.value()));
                    }

                    return clientResponse.bodyToMono(String.class)
                            .flatMap(response -> Mono.error(new MoviesInfoClientException(response, clientResponse.rawStatusCode())));
                })
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> {
                    log.info("Test LOG");
                    return Mono.error(new MoviesInfoServerException("Error Occured Sorry !!!!!"));
                })
                .bodyToMono(MovieInfo.class)
//                .retry(3)
                .retryWhen(RetryUtil.getRetrySpec())
                .log();
    }


}
