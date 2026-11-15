package com.joaovtmarques.auth.domain.refresh_token.usecase.revoke_all;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.joaovtmarques.auth.domain.refresh_token.repository.RefreshTokenRepository;

@ExtendWith(MockitoExtension.class)
class RevokeAllRefreshTokensUseCaseImplTest {

  @Mock
  private RefreshTokenRepository refreshTokenRepository;

  @InjectMocks
  private RevokeAllRefreshTokensUseCaseImpl useCase;

  @Test
  @DisplayName("Should delete all refresh tokens for the given userId")
  void shouldRevokeAllTokensForUser() {
    UUID userId = UUID.randomUUID();

    useCase.execute(new RevokeAllRefreshTokensCommand(userId));

    verify(refreshTokenRepository).deleteByUserId(userId);
  }

  @Test
  @DisplayName("Should do nothing when userId is null")
  void shouldDoNothingWhenUserIdIsNull() {
    assertDoesNotThrow(() -> useCase.execute(new RevokeAllRefreshTokensCommand(null)));

    verify(refreshTokenRepository, never()).deleteByUserId(any());
  }
}