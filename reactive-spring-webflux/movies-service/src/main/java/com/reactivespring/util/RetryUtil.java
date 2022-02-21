package com.reactivespring.util;

import com.reactivespring.exception.MoviesInfoServerException;
import com.reactivespring.exception.ReviewsServerException;
import reactor.core.Exceptions;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.function.Predicate;

public class RetryUtil {

    public static Retry getRetrySpec() {
        return Retry.fixedDelay(3, Duration.ofSeconds(1))
                .filter(new Predicate<Throwable>() {
                    @Override
                    public boolean test(Throwable throwable) {
                        return (throwable instanceof MoviesInfoServerException || throwable instanceof ReviewsServerException);
                    }
                })
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> Exceptions.propagate(retrySignal.failure()));
    }

}
