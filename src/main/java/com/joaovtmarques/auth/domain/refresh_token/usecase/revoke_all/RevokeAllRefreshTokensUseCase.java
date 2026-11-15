package com.joaovtmarques.auth.domain.refresh_token.usecase.revoke_all;

public interface RevokeAllRefreshTokensUseCase {
  void execute(RevokeAllRefreshTokensCommand command);
}