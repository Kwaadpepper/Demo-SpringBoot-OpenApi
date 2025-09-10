package com.openapi.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class CookieAuthenticationFailureException extends RuntimeException {
  public CookieAuthenticationFailureException(String message) {
    super(message);
  }

  public CookieAuthenticationFailureException(String message, Throwable cause) {
    super(message, cause);
  }
}
