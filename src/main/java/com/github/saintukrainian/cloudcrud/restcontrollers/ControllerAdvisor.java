package com.github.saintukrainian.cloudcrud.restcontrollers;

import com.github.saintukrainian.cloudcrud.exceptions.BadRequestException;
import com.github.saintukrainian.cloudcrud.exceptions.PersonDetailsNotFoundException;
import com.github.saintukrainian.cloudcrud.exceptions.PersonNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvisor {

    @ExceptionHandler(PersonNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public HttpStatus personNotFound(PersonNotFoundException e) {
        return HttpStatus.NOT_FOUND;
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public HttpStatus BadRequest(BadRequestException e) {
        return HttpStatus.BAD_REQUEST;
    }

    @ExceptionHandler(PersonDetailsNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public HttpStatus detailsNotFound(PersonDetailsNotFoundException e) {
        return HttpStatus.NOT_FOUND;
    }
}
