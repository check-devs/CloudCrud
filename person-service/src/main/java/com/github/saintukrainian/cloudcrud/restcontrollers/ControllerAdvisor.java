package com.github.saintukrainian.cloudcrud.restcontrollers;

import com.github.saintukrainian.cloudcrud.exceptions.BadRequestException;
import com.github.saintukrainian.cloudcrud.exceptions.PersonDetailsNotFoundException;
import com.github.saintukrainian.cloudcrud.exceptions.PersonNotFoundException;
import com.github.saintukrainian.cloudcrud.exceptions.ThreadExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Denys Matsenko
 * @version 1.0.0
 *     <p>The {@code ControllerAdvisor} class handles every custom exception <br>
 *     and sends back the appropriate response to the client.
 */
@RestControllerAdvice
public class ControllerAdvisor {

  /**
   * Handles {@code PersonNotFoundException} exceptions
   *
   * @param e {@code PersonNotFoundException}
   * @return HttpStatus.NOT_FOUND
   */
  @ExceptionHandler(PersonNotFoundException.class)
  @ResponseStatus(code = HttpStatus.NOT_FOUND)
  public HttpStatus personNotFound(PersonNotFoundException e) {
    return HttpStatus.NOT_FOUND;
  }

  /**
   * Handles {@code BadRequestException} exceptions
   *
   * @param e {@code BadRequestException}
   * @return HttpStatus.BAD_REQUEST
   */
  @ExceptionHandler(BadRequestException.class)
  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  public HttpStatus BadRequest(BadRequestException e) {
    return HttpStatus.BAD_REQUEST;
  }

  /**
   * Handles {@code PersonDetailsNotFoundException} exceptions
   *
   * @param e {@code PersonDetailsNotFoundException}
   * @return HttpStatus.NOT_FOUND
   */
  @ExceptionHandler(PersonDetailsNotFoundException.class)
  @ResponseStatus(code = HttpStatus.NOT_FOUND)
  public HttpStatus detailsNotFound(PersonDetailsNotFoundException e) {
    return HttpStatus.NOT_FOUND;
  }

  /**
   * Handles {@code ThreadExecutionException} exceptions
   *
   * @return HttpStatus.INTERNAL_SERVER_ERROR
   */
  @ExceptionHandler(ThreadExecutionException.class)
  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  public HttpStatus serverException() {
    return HttpStatus.INTERNAL_SERVER_ERROR;
  }
}
