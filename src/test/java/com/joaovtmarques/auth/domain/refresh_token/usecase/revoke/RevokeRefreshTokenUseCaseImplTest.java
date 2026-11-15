package com.joaovtmarques.auth.domain.refresh_token.usecase.revoke;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.joaovtmarques.auth.domain.common.service.HashService;
import com.joaovtmarques.auth.domain.refresh_token.model.RefreshToken;
import com.joaovtmarques.auth.domain.refresh_token.repository.RefreshTokenRepository;

@ExtendWith(MockitoExtension.class)
class RevokeRefreshTokenUseCaseImplTest {

  @Mock
  private RefreshTokenRepository refreshTokenRepository;

  @Mock
  private HashService hashService;

  @InjectMocks
  private RevokeRefreshTokenUseCaseImpl useCase;

  @Test
  @DisplayName("Should revoke active refresh token and save it")
  void shouldRevokeActiveRefreshToken() {
    String rawToken = "valid-refresh-token";
    String tokenHash = "hashed-valid-token";

    RefreshToken activeToken = new RefreshToken();
    activeToken.setId(UUID.randomUUID());
    activeToken.setTokenHash(tokenHash);
    activeToken.setExpiresAt(Instant.now().plus(1, ChronoUnit.DAYS));
    activeToken.setRevokedAt(null);

    when(hashService.hash(rawToken)).thenReturn(tokenHash);
    when(refreshTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(activeToken));

    useCase.execute(new RevokeRefreshTokenCommand(rawToken));

    assertTrue(activeToken.isRevoked());
    verify(refreshTokenRepository).save(activeToken);
  }

  @Test
  @DisplayName("Should do nothing when refresh token is already revoked (idempotent)")
  void shouldDoNothingWhenTokenIsAlreadyRevoked() {
    String rawToken = "already-revoked-token";
    String tokenHash = "hashed-revoked-token";

    RefreshToken revokedToken = new RefreshToken();
    revokedToken.setId(UUID.randomUUID());
    revokedToken.setTokenHash(tokenHash);
    revokedToken.setRevokedAt(Instant.now().minus(10, ChronoUnit.MINUTES));

    when(hashService.hash(rawToken)).thenReturn(tokenHash);
    when(refreshTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(revokedToken));

    assertDoesNotThrow(() -> useCase.execute(new RevokeRefreshTokenCommand(rawToken)));

    verify(refreshTokenRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should do nothing when refresh token does not exist (idempotent)")
  void shouldDoNothingWhenTokenDoesNotExist() {
    String rawToken = "non-existent-token";
    String tokenHash = "hashed-non-existent";

    when(hashService.hash(rawToken)).thenReturn(tokenHash);
    when(refreshTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.empty());

    assertDoesNotThrow(() -> useCase.execute(new RevokeRefreshTokenCommand(rawToken)));

    verify(refreshTokenRepository, never()).save(any());
  }
}