package com.joaovtmarques.auth.domain.refresh_token.usecase.revoke;

import com.joaovtmarques.auth.domain.common.service.HashService;
import com.joaovtmarques.auth.domain.refresh_token.repository.RefreshTokenRepository;

public class RevokeRefreshTokenUseCaseImpl implements RevokeRefreshTokenUseCase {

  private final RefreshTokenRepository refreshTokenRepository;
  private final HashService hashService;

  public RevokeRefreshTokenUseCaseImpl(
      RefreshTokenRepository refreshTokenRepository,
      HashService hashService) {
    this.refreshTokenRepository = refreshTokenRepository;
    this.hashService = hashService;
  }

  @Override
  public void execute(RevokeRefreshTokenCommand command) {
    if (command.refreshToken() == null || command.refreshToken().isBlank()) {
      return;
    }

    String tokenHash = hashService.hash(command.refreshToken());

    refreshTokenRepository.findByTokenHash(tokenHash).ifPresent(refreshToken -> {
      if (!refreshToken.isRevoked()) {
        refreshToken.revoke();
        refreshTokenRepository.save(refreshToken);
      }
    });
  }
}