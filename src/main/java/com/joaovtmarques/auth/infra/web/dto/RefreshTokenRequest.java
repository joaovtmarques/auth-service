package com.joaovtmarques.auth.infra.web.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
    @NotBlank(message = "The refresh token is mandatory") String refreshToken) {
}