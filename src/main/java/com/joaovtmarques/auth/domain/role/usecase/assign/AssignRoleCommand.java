package com.joaovtmarques.auth.domain.role.usecase.assign;

import java.util.UUID;

public record AssignRoleCommand(
    UUID userId,
    UUID roleId) {

}
