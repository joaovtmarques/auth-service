package com.joaovtmarques.auth.domain.role.usecase.remove;

import java.util.UUID;

public record RemoveRoleCommand(
    UUID userId,
    UUID roleId) {

}
