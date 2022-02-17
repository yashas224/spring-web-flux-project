package com.reactivespring.router;


import com.reactivespring.handler.ReviewHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ReviewRouter {

    @Bean
    public RouterFunction<ServerResponse> reviewsRoute(ReviewHandler reviewHandler) {
        return RouterFunctions.route()
                .path("/v1", builder -> {
                    builder.GET("/helloworld", (request -> ServerResponse.ok().bodyValue("Hello World")))
                            .nest(RequestPredicates.path("/reviews"), builder1 -> {
                                builder1
                                        .POST(request -> reviewHandler.addReview(request))
                                        .GET(request -> reviewHandler.getReviews(request))
                                        .PUT("/{id}", request -> reviewHandler.updateReview(request))
                                        .DELETE("/{id}", request -> reviewHandler.deleteReview(request))
                                        .build();
                            }).build();
                }).build();
    }
}
