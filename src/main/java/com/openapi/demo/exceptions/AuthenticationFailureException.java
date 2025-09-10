package com.openapi.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthenticationFailureException extends RuntimeException {
  public AuthenticationFailureException(String message) {
    super(message);
  }

  public AuthenticationFailureException(String message, Throwable cause) {
    super(message, cause);
  }
}
