package com.joaovtmarques.auth.domain.refresh_token.usecase.revoke;

public record RevokeRefreshTokenCommand(
    String refreshToken) {
}