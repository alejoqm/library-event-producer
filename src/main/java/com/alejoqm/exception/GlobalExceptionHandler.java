package com.alejoqm.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(LibraryEventException.class)
  public ResponseEntity libraryEventExceptionHandler(LibraryEventException e) {
    return new ResponseEntity<>(e.getErrorMessage(), e.getStatus());
  }
}
