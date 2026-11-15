package com.joaovtmarques.auth.domain.refresh_token.usecase.rotate;

public record RotateRefreshTokenResult(
    String accessToken,
    String refreshToken) {
}