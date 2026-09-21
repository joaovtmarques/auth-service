package com.joaovtmarques.auth.domain.refresh_token.usecase.create;

import java.time.Instant;
import java.util.UUID;

import com.joaovtmarques.auth.domain.refresh_token.model.RefreshToken;

public record CreateRefreshTokenCommand(
    UUID userId,
    String ipAddress,
    String userAgent) {

  public RefreshToken toModel(String tokenHash, Instant expiresAt) {
    RefreshToken refreshToken = new RefreshToken();
    refreshToken.setUserId(userId);
    refreshToken.setTokenHash(tokenHash);
    refreshToken.setExpiresAt(expiresAt);
    refreshToken.setIpAddress(ipAddress);
    refreshToken.setUserAgent(userAgent);
    return refreshToken;
  }

}