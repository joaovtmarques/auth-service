package com.joaovtmarques.auth.domain.role.usecase.create;

import com.joaovtmarques.auth.domain.role.model.Role;

public record CreateRoleCommand(
    String name,
    String description) {

  public Role toModel(String normalizedName) {
    Role role = new Role();
    role.setName(normalizedName);
    role.setDescription(description);
    return role;
  }

}
