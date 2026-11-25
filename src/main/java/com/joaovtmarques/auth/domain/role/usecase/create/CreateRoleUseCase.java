package com.joaovtmarques.auth.domain.role.usecase.create;

import com.joaovtmarques.auth.domain.role.model.Role;

public interface CreateRoleUseCase {

  Role execute(CreateRoleCommand command);

}
