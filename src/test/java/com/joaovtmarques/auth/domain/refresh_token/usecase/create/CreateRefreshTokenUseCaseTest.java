package com.joaovtmarques.auth.domain.refresh_token.usecase.create;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.joaovtmarques.auth.domain.common.service.HashService;
import com.joaovtmarques.auth.domain.refresh_token.model.RefreshToken;
import com.joaovtmarques.auth.domain.refresh_token.repository.RefreshTokenRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateRefreshTokenUseCaseTest {

  @Mock
  private RefreshTokenRepository refreshTokenRepository;

  @Mock
  private HashService hashService;

  @InjectMocks
  private CreateRefreshTokenUseCaseImpl createRefreshTokenUseCase;

  @Test
  @DisplayName("Should generate, hash, and persist a new RefreshToken successfully")
  void shouldExecuteCreateRefreshTokenSuccessfully() {
    UUID userId = UUID.randomUUID();
    CreateRefreshTokenCommand command = new CreateRefreshTokenCommand(userId, "127.0.0.1", "Mozilla/5.0");
    String expectedHash = "hashed_sha256_token";

    when(hashService.hash(anyString())).thenReturn(expectedHash);

    String rawToken = createRefreshTokenUseCase.execute(command);

    assertNotNull(rawToken);

    ArgumentCaptor<RefreshToken> tokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
    verify(refreshTokenRepository).save(tokenCaptor.capture());

    RefreshToken savedToken = tokenCaptor.getValue();
    assertEquals(userId, savedToken.getUserId());
    assertEquals(expectedHash, savedToken.getTokenHash());
    assertEquals("127.0.0.1", savedToken.getIpAddress());
    assertEquals("Mozilla/5.0", savedToken.getUserAgent());
    assertNotNull(savedToken.getExpiresAt());
  }
}