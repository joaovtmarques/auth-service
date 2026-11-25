package com.joaovtmarques.auth.domain.role.usecase.assign;

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

import com.joaovtmarques.auth.domain.role.exception.RoleNotFoundException;
import com.joaovtmarques.auth.domain.role.model.Role;
import com.joaovtmarques.auth.domain.role.repository.RoleRepository;
import com.joaovtmarques.auth.domain.user.exception.UserNotFoundException;
import com.joaovtmarques.auth.domain.user.model.User;
import com.joaovtmarques.auth.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AssignRoleToUserUseCaseImplTest {

  @Mock
  private RoleRepository roleRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private AssignRoleToUserUseCaseImpl useCase;

  @Test
  @DisplayName("Should assign role to user successfully")
  void shouldAssignRoleToUserSuccessfully() {
    UUID userId = UUID.randomUUID();
    UUID roleId = UUID.randomUUID();
    var command = new AssignRoleCommand(userId, roleId);

    var user = new User(userId, "João", "joao@email.com", "11999999999", "hash", Instant.now(), Instant.now());
    var role = new Role(roleId, "SARGENTEANTE", "Sargenteante", Instant.now());

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

    useCase.assignRoleToUser(command);

    assertTrue(user.getRoles().contains(role));
    verify(userRepository).save(user);
  }

  @Test
  @DisplayName("Should throw UserNotFoundException when user does not exist")
  void shouldThrowWhenUserNotFound() {
    UUID userId = UUID.randomUUID();
    UUID roleId = UUID.randomUUID();
    var command = new AssignRoleCommand(userId, roleId);

    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> useCase.assignRoleToUser(command));

    verifyNoInteractions(roleRepository);
    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should throw RoleNotFoundException when role does not exist")
  void shouldThrowWhenRoleNotFound() {
    UUID userId = UUID.randomUUID();
    UUID roleId = UUID.randomUUID();
    var command = new AssignRoleCommand(userId, roleId);
    var user = new User(userId, "João", "joao@email.com", "11999999999", "hash", Instant.now(), Instant.now());

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

    assertThrows(RoleNotFoundException.class, () -> useCase.assignRoleToUser(command));

    verify(userRepository, never()).save(any());
  }
}