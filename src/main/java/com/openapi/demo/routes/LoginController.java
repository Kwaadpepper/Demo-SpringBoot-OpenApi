package com.openapi.demo.routes;

import com.openapi.demo.StaticUser;
import com.openapi.demo.dto.ApiErrorDetails;
import com.openapi.demo.exceptions.AuthenticationFailureException;
import com.openapi.demo.exceptions.BadRequestException;
import com.openapi.demo.services.CookieService;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
  private final CookieService cookieService;

  public LoginController(CookieService cookieService) {
    this.cookieService = cookieService;
  }

  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = LoginRequest.class),
              examples = {
                @ExampleObject(
                    name = "Exemple de login",
                    value = "{\"login\": \"user.example\", \"password\": \"Password.1\"}")
              }))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully authenticated",
            headers = {
              @Header(
                  name = "set-cookie",
                  description = "Session cookie HTTP Only",
                  schema = @Schema(type = "string"))
            },
            content = @Content(schema = @Schema(implementation = ResponseDto.class))),
        @ApiResponse(
            responseCode = "401",
            description = "User could not be authenticated",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorDetails.class)))
      })
  @SecurityRequirements
  @PostMapping(
      value = "/api/auth/login",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ResponseDto> login(@Valid @RequestBody final LoginRequest request) {

    final var login = toLogin(request.login());
    final var password = toPassword(request.password());

    if (login.equals(StaticUser.USERNAME) && password.equals(StaticUser.PASSWORD)) {
      final var response = ResponseEntity.ok();
      final var sessionCookie = cookieService.generateCookie(StaticUser.UNIQ_ID);

      // Add cookie to response.
      response.header(HttpHeaders.SET_COOKIE, sessionCookie.toString());

      return response.body(new ResponseDto("User authenticated"));
    }

    throw new AuthenticationFailureException("Invalid login or password");
  }

  private String toLogin(@Nullable String login) {
    if (login == null || login.isBlank()) {
      throw new BadRequestException("Login cannot be empty");
    }

    return login;
  }

  private String toPassword(@Nullable String password) {
    if (password == null || password.isBlank()) {
      throw new BadRequestException("Password cannot be empty");
    }

    return password;
  }

  public record ResponseDto(String message) {}

  public record LoginRequest(String login, String password) {}
}
