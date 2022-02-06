package com.reactivespring.exceptionhandler;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<String> handleBeanValidations(WebExchangeBindException exception) {
        log.error("Exception From Controller is {}", exception.getMessage(), exception);

        String bindingResults = exception.getBindingResult()
                .getAllErrors()
                .stream()
                .map(objectError -> {
                    return objectError.getDefaultMessage();
                }).collect(Collectors.joining(","));
        log.error("ERROR- [}", bindingResults);
        return ResponseEntity.badRequest().body(bindingResults);
    }
}
