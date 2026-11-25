package com.joaovtmarques.auth.domain.role.usecase.list;

import java.util.List;

import com.joaovtmarques.auth.domain.role.model.Role;
import com.joaovtmarques.auth.domain.role.repository.RoleRepository;

public class ListRolesUseCaseImpl implements ListRolesUseCase {

  private final RoleRepository roleRepository;

  public ListRolesUseCaseImpl(RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }

  @Override
  public List<Role> listRoles() {
    return roleRepository.findAll();
  }

}
