package com.joaovtmarques.auth.infra.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthenticateUserRequest(
    @NotBlank(message = "Email is required") @Email(message = "Email must be valid") String email,

    @NotBlank(message = "Password is required") String password) {
}