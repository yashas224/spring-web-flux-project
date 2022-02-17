package com.reactivespring.routes;


import com.reactivespring.domain.Review;
import com.reactivespring.handler.ReviewHandler;
import com.reactivespring.repository.ReviewReactiveRepository;
import com.reactivespring.router.ReviewRouter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest
@ContextConfiguration(classes = {ReviewRouter.class, ReviewHandler.class})
public class ReviewsUnitTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    private ReviewReactiveRepository reviewReactiveRepository;


    @Test
    void addReview() {
        String revievId = "abcdefghij";
        Review reqReview = new Review(null, 1L, "Awesome yaaar", 10.0);
        Review savedReview = new Review(revievId, 1L, "Awesome yaaar", 10.0);

        Mockito.when(reviewReactiveRepository.save(Mockito.any(Review.class))).thenReturn(Mono.just(savedReview));

        webTestClient.post()
                .uri("/v1/reviews")
                .bodyValue(reqReview)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    var savedObj = reviewEntityExchangeResult.getResponseBody();
                    Assertions.assertEquals(revievId, savedObj.getReviewId());
                });

        Mockito.verify(reviewReactiveRepository, Mockito.times(1)).save(Mockito.any(Review.class));
    }

}
