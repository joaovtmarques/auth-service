package com.joaovtmarques.auth.infra.web.dto;

import java.time.Instant;
import java.util.UUID;

import com.joaovtmarques.auth.domain.role.model.Role;

public record RoleResponse(
    UUID id,
    String name,
    String description,
    Instant createdAt) {
  public static RoleResponse from(Role role) {
    return new RoleResponse(
        role.getId(),
        role.getName(),
        role.getDescription(),
        role.getCreatedAt());
  }
}