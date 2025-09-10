package com.openapi.demo.routes;

import com.openapi.demo.dto.ApiErrorDetails;
import com.openapi.demo.services.CookieService;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogoutController {
  private final CookieService cookieService;

  public LogoutController(CookieService cookieService) {
    this.cookieService = cookieService;
  }

  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            headers = {
              @Header(
                  name = "Set-Cookie",
                  description = "Session cookie HTTP Only",
                  schema = @Schema(type = "string"))
            },
            content = @Content(schema = @Schema(implementation = ResponseDto.class))),
        @ApiResponse(
            responseCode = "400",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorDetails.class)))
      })
  @PostMapping(value = "/api/auth/logout", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ResponseDto> logout(HttpServletRequest request) {

    final var jwtAccessToken = cookieService.getUniqIdFromRequest(request);
    if (jwtAccessToken == null) {
      return ResponseEntity.badRequest().body(new ResponseDto("No access token"));
    }

    final List<ResponseCookie> cookieList =
        List.of(cookieService.generateCookieRemoval(), cookieService.generateCookieRemoval());
    final var response = ResponseEntity.ok();

    cookieList.forEach(cookie -> response.header(HttpHeaders.SET_COOKIE, cookie.toString()));

    return response.body(new ResponseDto("success"));
  }

  public record ResponseDto(String message) {}

  public record LoginRequest(String login, String password) {}
}
