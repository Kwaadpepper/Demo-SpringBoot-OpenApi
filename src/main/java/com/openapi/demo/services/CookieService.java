package com.openapi.demo.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.springframework.http.ResponseCookie;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

/** Service to manage cookies, including creation, removal, and extraction of unique identifiers. */
@Component
public class CookieService {
  private static final String COOKIE_NAME = "static-cookie";
  public static final String COOKIE_HTTP_PATH = "/";
  private static final Integer COOKIE_EXP_IN_MINUTES = 60;

  /**
   * Generate a new cookie with the given uniqId as value.
   *
   * @param uniqId The unique identifier to be stored in the cookie.
   * @return A ResponseCookie object representing the generated cookie.
   */
  public ResponseCookie generateCookie(final String uniqId) {
    return CookieService.this.generateCookie(
        COOKIE_NAME, uniqId, COOKIE_HTTP_PATH, COOKIE_EXP_IN_MINUTES);
  }

  /**
   * Generate a cookie that will remove the cookie from the client.
   *
   * @return A ResponseCookie object representing the cookie removal.
   */
  public ResponseCookie generateCookieRemoval() {
    return CookieService.this.generateCookie(COOKIE_NAME, null, COOKIE_HTTP_PATH);
  }

  /**
   * Extract the unique identifier from the request's cookies.
   *
   * @param request The HttpServletRequest object containing the cookies.
   * @return The unique identifier if present, otherwise null.
   */
  public @Nullable String getUniqIdFromRequest(final HttpServletRequest request) {
    return getCookieValueByName(request, COOKIE_NAME)
        .map(Cookie::getValue)
        .filter(cookieValue -> cookieValue != null && !cookieValue.isBlank())
        .map(String::new)
        .orElse(null);
  }

  /** Helper method to generate a cookie with specified parameters. */
  private ResponseCookie generateCookie(String name, @Nullable String value, String path) {
    return CookieService.this.generateCookie(name, value, path, 0);
  }

  /** Helper method to generate a cookie with specified parameters. */
  private ResponseCookie generateCookie(
      String name, String value, String path, Integer expirationInMinutes) {
    return ResponseCookie.from(name)
        .value(value)
        .path(path)
        .maxAge(expirationInMinutes * 60L)
        .httpOnly(true)
        .build();
  }

  /** Helper method to retrieve a cookie by its name from the request. */
  private Optional<Cookie> getCookieValueByName(HttpServletRequest request, String name) {
    return Optional.ofNullable(WebUtils.getCookie(request, name));
  }
}
