package com.joaovtmarques.auth.domain.refresh_token.usecase.rotate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.joaovtmarques.auth.domain.common.service.HashService;
import com.joaovtmarques.auth.domain.refresh_token.exception.InvalidRefreshTokenException;
import com.joaovtmarques.auth.domain.refresh_token.model.RefreshToken;
import com.joaovtmarques.auth.domain.refresh_token.repository.RefreshTokenRepository;
import com.joaovtmarques.auth.domain.refresh_token.usecase.create.CreateRefreshTokenCommand;
import com.joaovtmarques.auth.domain.refresh_token.usecase.create.CreateRefreshTokenUseCase;
import com.joaovtmarques.auth.domain.user.service.TokenService;

@ExtendWith(MockitoExtension.class)
class RotateRefreshTokenUseCaseImplTest {

  @Mock
  private RefreshTokenRepository refreshTokenRepository;

  @Mock
  private HashService hashService;

  @Mock
  private TokenService tokenService;

  @Mock
  private CreateRefreshTokenUseCase createRefreshTokenUseCase;

  @InjectMocks
  private RotateRefreshTokenUseCaseImpl useCase;

  private UUID userId;
  private String rawOldToken;
  private String hashedOldToken;
  private RotateRefreshTokenCommand command;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    rawOldToken = "old-plain-refresh-token";
    hashedOldToken = "hashed-old-token";
    command = new RotateRefreshTokenCommand(rawOldToken, "127.0.0.1", "Mozilla/5.0");
  }

  @Test
  @DisplayName("Should rotate refresh token successfully when token is valid and active")
  void shouldRotateRefreshTokenSuccessfully() {
    RefreshToken activeToken = new RefreshToken();
    activeToken.setId(UUID.randomUUID());
    activeToken.setUserId(userId);
    activeToken.setTokenHash(hashedOldToken);
    activeToken.setExpiresAt(Instant.now().plus(1, ChronoUnit.DAYS));
    activeToken.setRevokedAt(null);

    String newRawToken = "new-plain-refresh-token";
    String newHashedToken = "hashed-new-token";
    String newAccessToken = "new-jwt-access-token";

    when(hashService.hash(rawOldToken)).thenReturn(hashedOldToken);
    when(refreshTokenRepository.findByTokenHash(hashedOldToken)).thenReturn(Optional.of(activeToken));
    when(createRefreshTokenUseCase.execute(any(CreateRefreshTokenCommand.class))).thenReturn(newRawToken);
    when(hashService.hash(newRawToken)).thenReturn(newHashedToken);
    when(tokenService.generateToken(userId.toString())).thenReturn(newAccessToken);

    RotateRefreshTokenResult result = useCase.execute(command);

    assertNotNull(result);
    assertEquals(newAccessToken, result.accessToken());
    assertEquals(newRawToken, result.refreshToken());
    assertTrue(activeToken.isRevoked());
    assertEquals(newHashedToken, activeToken.getReplacedByTokenHash());

    verify(refreshTokenRepository).save(activeToken);
    verify(refreshTokenRepository, never()).deleteByUserId(any());
  }

  @Test
  @DisplayName("Should throw InvalidRefreshTokenException when token hash is not found")
  void shouldThrowExceptionWhenTokenNotFound() {
    when(hashService.hash(rawOldToken)).thenReturn(hashedOldToken);
    when(refreshTokenRepository.findByTokenHash(hashedOldToken)).thenReturn(Optional.empty());

    assertThrows(InvalidRefreshTokenException.class, () -> useCase.execute(command));

    verify(refreshTokenRepository, never()).save(any());
    verify(createRefreshTokenUseCase, never()).execute(any());
  }

  @Test
  @DisplayName("Should delete all user tokens and throw exception when token reuse is detected")
  void shouldRevokeAllUserSessionsOnTokenReuse() {
    RefreshToken revokedToken = new RefreshToken();
    revokedToken.setId(UUID.randomUUID());
    revokedToken.setUserId(userId);
    revokedToken.setTokenHash(hashedOldToken);
    revokedToken.setExpiresAt(Instant.now().plus(1, ChronoUnit.DAYS));
    revokedToken.setRevokedAt(Instant.now().minus(5, ChronoUnit.MINUTES));

    when(hashService.hash(rawOldToken)).thenReturn(hashedOldToken);
    when(refreshTokenRepository.findByTokenHash(hashedOldToken)).thenReturn(Optional.of(revokedToken));

    assertThrows(InvalidRefreshTokenException.class, () -> useCase.execute(command));

    verify(refreshTokenRepository).deleteByUserId(userId);
    verify(createRefreshTokenUseCase, never()).execute(any());
    verify(tokenService, never()).generateToken(any());
  }

  @Test
  @DisplayName("Should throw InvalidRefreshTokenException when token is expired")
  void shouldThrowExceptionWhenTokenIsExpired() {
    RefreshToken expiredToken = new RefreshToken();
    expiredToken.setId(UUID.randomUUID());
    expiredToken.setUserId(userId);
    expiredToken.setTokenHash(hashedOldToken);
    expiredToken.setExpiresAt(Instant.now().minus(1, ChronoUnit.MINUTES)); // Expirado
    expiredToken.setRevokedAt(null);

    when(hashService.hash(rawOldToken)).thenReturn(hashedOldToken);
    when(refreshTokenRepository.findByTokenHash(hashedOldToken)).thenReturn(Optional.of(expiredToken));

    assertThrows(InvalidRefreshTokenException.class, () -> useCase.execute(command));

    verify(refreshTokenRepository, never()).save(any());
    verify(refreshTokenRepository, never()).deleteByUserId(any());
    verify(createRefreshTokenUseCase, never()).execute(any());
  }
}