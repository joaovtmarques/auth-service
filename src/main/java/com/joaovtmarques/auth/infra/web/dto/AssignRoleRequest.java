package com.joaovtmarques.auth.infra.web.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record AssignRoleRequest(
    @NotNull(message = "Role ID is required") UUID roleId) {
}