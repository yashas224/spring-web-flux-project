package com.reactivespring.client;

import com.reactivespring.domain.Review;
import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.MoviesInfoServerException;
import com.reactivespring.exception.ReviewsClientException;
import com.reactivespring.exception.ReviewsServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ReviewsRestClient {

    @Autowired
    private WebClient webClient;

    @Value("${restClient.reviewsURL}")
    private String url;


    public Flux<Review> retriveRevew(String movieId) {
        var finalUrl = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("movieInfoId", movieId)
                .buildAndExpand().toString();

        return webClient
                .get()
                .uri(finalUrl)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> {
                    if (clientResponse.statusCode() == HttpStatus.NOT_FOUND) {
                        return Mono.empty();
                    }
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(response -> Mono.error(new ReviewsClientException(response)));
                })
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> {
                    return Mono.error(new ReviewsServerException("Error Occured Sorry !!!!!"));
                })
                .bodyToFlux(Review.class)
                .log();
    }

}
