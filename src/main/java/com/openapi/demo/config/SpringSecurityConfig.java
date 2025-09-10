package com.openapi.demo.config;

import com.openapi.demo.components.StaticCredentialFilter;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SpringSecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(
      HttpSecurity http, StaticCredentialFilter staticCredentialFilter) throws Exception {

    final List<String> routesToIgnore =
        List.of(
            "/api/auth/login",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/actuator/**");

    return http.sessionManagement(
            // No cookie session, just state less API.
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // No CSRF for stateless APIs.
        .csrf(AbstractHttpConfigurer::disable)
        // 401 on unauthenticated requests.
        .exceptionHandling(
            handling ->
                handling.authenticationEntryPoint(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
        // All requests must be authenticated unless explicitly ignored.
        .authorizeHttpRequests(
            request -> {
              // Allow non protected AuthRequestToUrls are not protected.
              request.requestMatchers(routesToIgnore.toArray(String[]::new)).permitAll();

              // Any other routes are.
              request.anyRequest().fullyAuthenticated();
            })
        // Custom filter to process our static user authentication.
        .addFilterAt(staticCredentialFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }
}
