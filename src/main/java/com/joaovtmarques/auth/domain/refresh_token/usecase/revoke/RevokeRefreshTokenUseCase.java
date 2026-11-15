package com.joaovtmarques.auth.domain.refresh_token.usecase.revoke;

public interface RevokeRefreshTokenUseCase {
  void execute(RevokeRefreshTokenCommand command);
}