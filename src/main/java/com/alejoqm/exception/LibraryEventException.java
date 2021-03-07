package com.alejoqm.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class LibraryEventException extends Throwable {

  private HttpStatus status;
  private String errorMessage;
}
