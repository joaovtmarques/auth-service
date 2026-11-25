package com.joaovtmarques.auth.domain.role.usecase.remove;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.joaovtmarques.auth.domain.role.exception.LastMasterAdminRemovalException;
import com.joaovtmarques.auth.domain.role.exception.RoleNotFoundException;
import com.joaovtmarques.auth.domain.role.model.Role;
import com.joaovtmarques.auth.domain.role.repository.RoleRepository;
import com.joaovtmarques.auth.domain.user.exception.UserNotFoundException;
import com.joaovtmarques.auth.domain.user.model.User;
import com.joaovtmarques.auth.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class RemoveRoleFromUserUseCaseImplTest {

  @Mock
  private RoleRepository roleRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private RemoveRoleFromUserUseCaseImpl useCase;

  @Test
  @DisplayName("Should remove non-master role from user successfully")
  void shouldRemoveRoleSuccessfully() {
    UUID userId = UUID.randomUUID();
    UUID roleId = UUID.randomUUID();
    var command = new RemoveRoleCommand(userId, roleId);

    var role = new Role(roleId, "SARGENTEANTE", "Sargenteante", Instant.now());
    var user = new User(userId, "João", "joao@email.com", "11999999999", "hash", Instant.now(), Instant.now());
    user.assignRole(role);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

    useCase.removeRoleFromUser(command);

    assertTrue(user.getRoles().isEmpty());
    verify(userRepository).save(user);
  }

  @Test
  @DisplayName("Should throw LastMasterAdminRemovalException when trying to remove the last AUTH_MASTER")
  void shouldThrowWhenRemovingLastMasterAdmin() {
    UUID userId = UUID.randomUUID();
    UUID roleId = UUID.randomUUID();
    var command = new RemoveRoleCommand(userId, roleId);

    var masterRole = new Role(roleId, "AUTH_MASTER", "Master", Instant.now());
    var user = new User(userId, "Master", "master@auth.local", "11999999999", "hash", Instant.now(), Instant.now());
    user.assignRole(masterRole);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(roleRepository.findById(roleId)).thenReturn(Optional.of(masterRole));
    when(userRepository.countByRoleName("AUTH_MASTER")).thenReturn(1L);

    assertThrows(LastMasterAdminRemovalException.class, () -> useCase.removeRoleFromUser(command));

    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should allow removing AUTH_MASTER when there is more than one master")
  void shouldAllowRemovingMasterWhenMultipleMastersExist() {
    UUID userId = UUID.randomUUID();
    UUID roleId = UUID.randomUUID();
    var command = new RemoveRoleCommand(userId, roleId);

    var masterRole = new Role(roleId, "AUTH_MASTER", "Master", Instant.now());
    var user = new User(userId, "Master 2", "master2@auth.local", "11999999999", "hash", Instant.now(), Instant.now());
    user.assignRole(masterRole);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(roleRepository.findById(roleId)).thenReturn(Optional.of(masterRole));
    when(userRepository.countByRoleName("AUTH_MASTER")).thenReturn(2L);

    useCase.removeRoleFromUser(command);

    assertTrue(user.getRoles().isEmpty());
    verify(userRepository).save(user);
  }

  @Test
  @DisplayName("Should throw UserNotFoundException when user does not exist")
  void shouldThrowWhenUserNotFound() {
    var command = new RemoveRoleCommand(UUID.randomUUID(), UUID.randomUUID());
    when(userRepository.findById(command.userId())).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> useCase.removeRoleFromUser(command));
    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should throw RoleNotFoundException when role does not exist")
  void shouldThrowWhenRoleNotFound() {
    UUID userId = UUID.randomUUID();
    var command = new RemoveRoleCommand(userId, UUID.randomUUID());
    var user = new User(userId, "João", "joao@email.com", "11999999999", "hash", Instant.now(), Instant.now());

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(roleRepository.findById(command.roleId())).thenReturn(Optional.empty());

    assertThrows(RoleNotFoundException.class, () -> useCase.removeRoleFromUser(command));
    verify(userRepository, never()).save(any());
  }
}