package com.joaovtmarques.auth.domain.role.usecase.create;

import com.joaovtmarques.auth.domain.role.exception.RoleAlreadyExistsException;
import com.joaovtmarques.auth.domain.role.model.Role;
import com.joaovtmarques.auth.domain.role.repository.RoleRepository;

public class CreateRoleUseCaseImpl implements CreateRoleUseCase {

  private final RoleRepository roleRepository;

  public CreateRoleUseCaseImpl(RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }

  @Override
  public Role execute(CreateRoleCommand command) {
    String normalizedRoleName = command.name().trim().toUpperCase();

    if (roleRepository.existsByName(normalizedRoleName)) {
      throw new RoleAlreadyExistsException("Role with name " + normalizedRoleName + " already exists.");
    }

    var role = command.toModel(normalizedRoleName);
    return roleRepository.save(role);
  }

}
