package com.joaovtmarques.auth.infra.web.dto;

public record RefreshTokenResponse(
    String accessToken,
    String refreshToken,
    String tokenType) {
  public RefreshTokenResponse(String accessToken, String refreshToken) {
    this(accessToken, refreshToken, "Bearer");
  }
}