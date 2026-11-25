package com.joaovtmarques.auth.infra.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateRoleRequest(
    @NotBlank(message = "Role name is required") @Size(max = 100, message = "Role name must be at most 100 characters") String name,

    @Size(max = 255, message = "Description must be at most 255 characters") String description) {
}