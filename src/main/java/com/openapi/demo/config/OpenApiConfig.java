package com.openapi.demo.config;

import com.openapi.demo.services.CookieService;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  OpenAPI openApi() {
    return new OpenAPI()
        .addSecurityItem(new SecurityRequirement().addList("Cookie Authentication"))
        .components(
            new Components().addSecuritySchemes("Cookie Authentication", createApiCookieScheme()))
        .info(
            new Info()
                .title("Demo OpenApi")
                .description("Support Service Api written as an OpenClassRoom project.")
                .version("0.0.1")
                .contact(
                    new Contact()
                        .name("Munsch Jeremy")
                        .email("github@jeremydev.ovh")
                        .url("https://jeremydev.ovh")));
  }

  private SecurityScheme createApiCookieScheme() {
    return new SecurityScheme()
        .type(SecurityScheme.Type.APIKEY)
        .in(SecurityScheme.In.COOKIE)
        .name(CookieService.COOKIE_NAME);
  }
}
