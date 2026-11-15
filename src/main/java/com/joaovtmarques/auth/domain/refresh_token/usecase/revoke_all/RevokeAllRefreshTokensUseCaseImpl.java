package com.joaovtmarques.auth.domain.refresh_token.usecase.revoke_all;

import com.joaovtmarques.auth.domain.refresh_token.repository.RefreshTokenRepository;

public class RevokeAllRefreshTokensUseCaseImpl implements RevokeAllRefreshTokensUseCase {

  private final RefreshTokenRepository refreshTokenRepository;

  public RevokeAllRefreshTokensUseCaseImpl(RefreshTokenRepository refreshTokenRepository) {
    this.refreshTokenRepository = refreshTokenRepository;
  }

  @Override
  public void execute(RevokeAllRefreshTokensCommand command) {
    if (command.userId() == null) {
      return;
    }
    refreshTokenRepository.deleteByUserId(command.userId());
  }
}