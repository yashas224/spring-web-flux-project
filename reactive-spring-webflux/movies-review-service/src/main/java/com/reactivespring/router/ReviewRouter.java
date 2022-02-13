package com.reactivespring.router;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ReviewRouter {

    @Bean
    public RouterFunction<ServerResponse> reviewsRoute() {
        return RouterFunctions.route()
                .GET("/v1/helloworld", (request -> {
                    return ServerResponse.ok().bodyValue("Hello World");
                })).build();
    }
}
