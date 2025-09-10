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
            "/api/auth/logout",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**");

    return http.sessionManagement(
            // No cookie session, just state less API.
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // No CSRF for stateless APIs.
        .csrf(AbstractHttpConfigurer::disable)
        .exceptionHandling(
            handling ->
                handling.authenticationEntryPoint(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
        .authorizeHttpRequests(
            request -> {
              // Allow non protected AuthRequestToUrls are not protected.
              request.requestMatchers(routesToIgnore.toArray(String[]::new)).permitAll();

              // Allow root path.
              request.requestMatchers("/").permitAll();

              // Any other routes are.
              request.anyRequest().fullyAuthenticated();
            })
        .addFilterAt(staticCredentialFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }
}
