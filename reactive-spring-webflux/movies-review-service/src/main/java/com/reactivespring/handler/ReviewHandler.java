package com.reactivespring.handler;


import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class ReviewHandler {

    @Autowired
    ReviewReactiveRepository reviewReactiveRepository;

    public Mono<ServerResponse> addReview(ServerRequest request) {
        return request.bodyToMono(Review.class)
                .flatMap(review -> reviewReactiveRepository.save(review))
                .flatMap((review -> ServerResponse.status(HttpStatus.CREATED).bodyValue(review)));
    }

    public Mono<ServerResponse> getReviews(ServerRequest request) {
        Optional<String> movieInfoId = request.queryParam("movieInfoId");
        if (movieInfoId.isEmpty()) {
            var flux = reviewReactiveRepository.findAll();
            return ServerResponse.ok().body(flux, Review.class);
        } else {
            var flux = reviewReactiveRepository.findByMovieInfoId(Long.parseLong(movieInfoId.get()));
            return ServerResponse.ok().body(flux, Review.class);
        }

    }

    public Mono<ServerResponse> updateReview(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<Review> reviewMono = reviewReactiveRepository.findById(id);

        return reviewMono.flatMap(DBReview ->
                request.bodyToMono(Review.class)
                        .map(reqReview -> {
                            DBReview.setComment(reqReview.getComment());
                            DBReview.setMovieInfoId(reqReview.getMovieInfoId());
                            DBReview.setRating(reqReview.getRating());
                            return DBReview;
                        }).flatMap(ToBesavedReview -> {
                            return reviewReactiveRepository.save(ToBesavedReview);
                        }).flatMap(savedReview -> ServerResponse.ok().bodyValue(savedReview))
        ).switchIfEmpty(ServerResponse.notFound().build());

    }

    public Mono<ServerResponse> deleteReview(ServerRequest request) {
        String id = request.pathVariable("id");
        var savedReview = reviewReactiveRepository.findById(id);
        return savedReview.flatMap(review ->
                reviewReactiveRepository.delete(review)
                        .then(ServerResponse.noContent().build())
        ).switchIfEmpty(ServerResponse.notFound().build());
    }
}
