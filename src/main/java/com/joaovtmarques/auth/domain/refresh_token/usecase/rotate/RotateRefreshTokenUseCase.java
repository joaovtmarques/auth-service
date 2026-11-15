package com.joaovtmarques.auth.domain.refresh_token.usecase.rotate;

public interface RotateRefreshTokenUseCase {
  RotateRefreshTokenResult execute(RotateRefreshTokenCommand command);
}