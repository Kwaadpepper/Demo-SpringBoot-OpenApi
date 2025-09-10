package com.openapi.demo.routes;

import com.openapi.demo.StaticUser;
import com.openapi.demo.dto.ApiErrorDetails;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserInfoController {

  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(
            responseCode = "400",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorDetails.class)))
      })
  @GetMapping(value = "/api/userinfo", produces = "application/json")
  UserDto getUserInfo(Authentication authentication) {
    final var userDetails = (StaticUser) authentication.getPrincipal();
    final var userAuthority = authentication.getAuthorities().stream().findFirst();

    if (userAuthority.isEmpty()) {
      throw new IllegalStateException("User has no role assigned");
    }

    return new UserDto(userDetails.getFullName(), userAuthority.orElseThrow().getAuthority());
  }

  public record UserDto(String fullName, String role) {}
}
