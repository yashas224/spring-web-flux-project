package com.reactivespring.client;

import com.reactivespring.domain.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
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
                .bodyToFlux(Review.class)
                .log();
    }

}
