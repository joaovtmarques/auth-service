package com.joaovtmarques.auth.domain.refresh_token.usecase.revoke_all;

import java.util.UUID;

public record RevokeAllRefreshTokensCommand(
    UUID userId) {
}