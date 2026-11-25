package com.joaovtmarques.auth.domain.role.usecase.remove;

import com.joaovtmarques.auth.domain.role.exception.LastMasterAdminRemovalException;
import com.joaovtmarques.auth.domain.role.exception.RoleNotFoundException;
import com.joaovtmarques.auth.domain.role.repository.RoleRepository;
import com.joaovtmarques.auth.domain.user.exception.UserNotFoundException;
import com.joaovtmarques.auth.domain.user.repository.UserRepository;

public class RemoveRoleFromUserUseCaseImpl implements RemoveRoleFromUserUseCase {

  private final RoleRepository roleRepository;
  private final UserRepository userRepository;

  public RemoveRoleFromUserUseCaseImpl(RoleRepository roleRepository, UserRepository userRepository) {
    this.roleRepository = roleRepository;
    this.userRepository = userRepository;
  }

  @Override
  public void removeRoleFromUser(RemoveRoleCommand command) {

    var user = userRepository.findById(command.userId())
        .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + command.userId()));
    var role = roleRepository.findById(command.roleId())
        .orElseThrow(() -> new RoleNotFoundException("Role not found with ID: " + command.roleId()));

    if ("AUTH_MASTER".equals(role.getName())) {
      long masterCount = userRepository.countByRoleName("AUTH_MASTER");
      boolean userHasMaster = user.getRoles().stream()
          .anyMatch(r -> "AUTH_MASTER".equals(r.getName()));

      if (userHasMaster && masterCount <= 1) {
        throw new LastMasterAdminRemovalException("Cannot remove the last AUTH_MASTER role from the system.");
      }
    }

    user.removeRole(role.getName());
    userRepository.save(user);
  }

}
