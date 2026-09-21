package com.joaovtmarques.auth.domain.refresh_token.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RefreshTokenTest {

  @Test
  @DisplayName("Should return active true when token is neither expired nor revoked")
  void shouldBeActiveWhenNotExpiredAndNotRevoked() {
    RefreshToken token = new RefreshToken();
    token.setExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS));

    assertTrue(token.isActive());
    assertFalse(token.isExpired());
    assertFalse(token.isRevoked());
  }

  @Test
  @DisplayName("Should return expired true when expiration date is in the past")
  void shouldBeExpiredWhenExpiresAtIsInPast() {
    RefreshToken token = new RefreshToken();
    token.setExpiresAt(Instant.now().minus(1, ChronoUnit.MINUTES));

    assertTrue(token.isExpired());
    assertFalse(token.isActive());
  }

  @Test
  @DisplayName("Should revoke token correctly and update replacedByTokenHash")
  void shouldRevokeAndReplaceToken() {
    RefreshToken token = new RefreshToken();
    token.setExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS));

    token.replaceWith("new_token_hash_456");

    assertTrue(token.isRevoked());
    assertNotNull(token.getRevokedAt());
    assertFalse(token.isActive());
    assertTrue("new_token_hash_456".equals(token.getReplacedByTokenHash()));
  }
}