package com.joaovtmarques.auth.domain.role.usecase.assign;

import com.joaovtmarques.auth.domain.role.exception.RoleNotFoundException;
import com.joaovtmarques.auth.domain.role.repository.RoleRepository;
import com.joaovtmarques.auth.domain.user.exception.UserNotFoundException;
import com.joaovtmarques.auth.domain.user.repository.UserRepository;

public class AssignRoleToUserUseCaseImpl implements AssignRoleToUserUseCase {

  private final RoleRepository roleRepository;
  private final UserRepository userRepository;

  public AssignRoleToUserUseCaseImpl(RoleRepository roleRepository, UserRepository userRepository) {
    this.roleRepository = roleRepository;
    this.userRepository = userRepository;
  }

  @Override
  public void assignRoleToUser(AssignRoleCommand command) {
    var user = userRepository.findById(command.userId())
        .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + command.userId()));
    var role = roleRepository.findById(command.roleId())
        .orElseThrow(() -> new RoleNotFoundException("Role not found with ID: " + command.roleId()));

    user.assignRole(role);
    userRepository.save(user);
  }

}
