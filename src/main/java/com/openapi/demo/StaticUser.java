package com.openapi.demo;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public final class StaticUser implements UserDetails {
  public static final String UNIQ_ID = UUID.randomUUID().toString();
  public static final String USERNAME = "user.example";
  public static final String PASSWORD = "Password.1";
  public static final String ROLE = "USER";
  private static final String FULL_NAME = "John Doe";

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new Authority(ROLE));
  }

  @Override
  public String getPassword() {
    return PASSWORD;
  }

  @Override
  public String getUsername() {
    return USERNAME;
  }

  public String getFullName() {
    return FULL_NAME;
  }

  public record Authority(String role) implements GrantedAuthority {
    @Override
    public String getAuthority() {
      return role;
    }
  }
}
