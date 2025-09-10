package com.openapi.demo.exceptions;

import com.openapi.demo.dto.ApiErrorDetails;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ApiErrorDetails> handleException(
      final BadRequestException ex, final WebRequest request) {
    return new ResponseEntity<>(toErrorDetails(ex, request), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(CookieAuthenticationFailureException.class)
  public ResponseEntity<ApiErrorDetails> handleException(
      final CookieAuthenticationFailureException ex, final WebRequest request) {
    return new ResponseEntity<>(toErrorDetails(ex, request), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(AuthenticationFailureException.class)
  public ResponseEntity<ApiErrorDetails> handleException(
      final AuthenticationFailureException ex, final WebRequest request) {
    return new ResponseEntity<>(toErrorDetails(ex, request), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(Throwable.class)
  public ResponseEntity<ApiErrorDetails> handleException(
      final Throwable ex, final WebRequest request) {
    logger.fatal("Error : '%s' on uri '%s'".formatted(ex.getMessage(), getRequestUri(request)), ex);
    return new ResponseEntity<>(
        toErrorDetails("Server error", request), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private String getRequestUri(final WebRequest request) {
    return ((ServletWebRequest) request).getRequest().getRequestURI();
  }

  private ApiErrorDetails toErrorDetails(final String message, final WebRequest request) {
    return new ApiErrorDetails(message, ZonedDateTime.now(ZoneId.systemDefault()));
  }

  private ApiErrorDetails toErrorDetails(final Throwable e, final WebRequest request) {
    return new ApiErrorDetails(
        e.getMessage() != null ? e.getMessage() : "error",
        ZonedDateTime.now(ZoneId.systemDefault()));
  }
}
