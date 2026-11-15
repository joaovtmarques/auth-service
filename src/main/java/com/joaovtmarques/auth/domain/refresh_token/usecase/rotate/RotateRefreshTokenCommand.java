package com.joaovtmarques.auth.domain.refresh_token.usecase.rotate;

public record RotateRefreshTokenCommand(
        String refreshToken,
        String ipAddress,
        String userAgent) {
}