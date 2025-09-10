package com.openapi.demo.components;

import com.openapi.demo.StaticUser;
import com.openapi.demo.exceptions.CookieAuthenticationFailureException;
import com.openapi.demo.services.CookieService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class StaticCredentialFilter extends OncePerRequestFilter {
  private final CookieService cookieService;
  private final HandlerExceptionResolver exceptionResolver;

  public StaticCredentialFilter(
      CookieService cookieService,
      @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) {
    this.cookieService = cookieService;
    this.exceptionResolver = exceptionResolver;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse servletResponse, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      final var uniqId = cookieService.getUniqIdFromRequest(request);

      if (uniqId == null) {
        logger.debug("Request missing 'Cookie' header with jwt token, skipping authentication.");
        filterChain.doFilter(request, servletResponse);
        return;
      }

      final var userDetails = getUserDetailsFromUniqIdToken(uniqId);

      if (userDetails == null) {
        throw new CookieAuthenticationFailureException("Invalid uniqId token.");
      }

      final SecurityContext securityContext = getNewSecurityContext(request, userDetails, uniqId);
      SecurityContextHolder.setContext(securityContext);

      // Pursue the filter chain.
      filterChain.doFilter(request, servletResponse);
    } catch (CookieAuthenticationFailureException e) {
      // Dispatch the exception to our Global handler.
      exceptionResolver.resolveException(request, servletResponse, null, e);
    } catch (Exception e) {
      logger.error("Error during filter processing");
      // Dispatch the exception to our Global handler.
      exceptionResolver.resolveException(request, servletResponse, null, e);
    }
  }

  private SecurityContext getNewSecurityContext(
      HttpServletRequest request, UserDetails userDetails, String uniqId) {
    final var context = SecurityContextHolder.createEmptyContext();
    final var authToken =
        new UsernamePasswordAuthenticationToken(userDetails, uniqId, userDetails.getAuthorities());

    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

    context.setAuthentication(authToken);
    return context;
  }

  private @Nullable UserDetails getUserDetailsFromUniqIdToken(String uniqId) {
    if (StaticUser.UNIQ_ID.equals(uniqId)) {
      return new StaticUser();
    }
    return null;
  }
}
