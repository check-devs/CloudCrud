package com.github.saintukrainian.cloudcrud.restcontrollers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvisor {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public HttpStatus notFound(IllegalArgumentException e) {
        return HttpStatus.NOT_FOUND;
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public HttpStatus BadRequest(IllegalStateException e) {
        return HttpStatus.BAD_REQUEST;
    }
}
