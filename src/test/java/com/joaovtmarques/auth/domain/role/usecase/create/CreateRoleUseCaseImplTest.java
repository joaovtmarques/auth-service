package com.joaovtmarques.auth.domain.role.usecase.create;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.joaovtmarques.auth.domain.role.exception.RoleAlreadyExistsException;
import com.joaovtmarques.auth.domain.role.model.Role;
import com.joaovtmarques.auth.domain.role.repository.RoleRepository;

@ExtendWith(MockitoExtension.class)
class CreateRoleUseCaseImplTest {

  @Mock
  private RoleRepository roleRepository;

  @InjectMocks
  private CreateRoleUseCaseImpl useCase;

  @Test
  @DisplayName("Should create role successfully normalizing name to uppercase")
  void shouldCreateRoleSuccessfully() {
    var command = new CreateRoleCommand("  sargenteante ", "Gerencia escalas e viaturas");
    var savedRole = new Role(UUID.randomUUID(), "SARGENTEANTE", "Gerencia escalas e viaturas", Instant.now());

    when(roleRepository.existsByName("SARGENTEANTE")).thenReturn(false);
    when(roleRepository.save(any(Role.class))).thenReturn(savedRole);

    Role result = useCase.execute(command);

    assertNotNull(result);
    assertEquals("SARGENTEANTE", result.getName());
    assertEquals("Gerencia escalas e viaturas", result.getDescription());
    verify(roleRepository).existsByName("SARGENTEANTE");
    verify(roleRepository).save(any(Role.class));
  }

  @Test
  @DisplayName("Should throw RoleAlreadyExistsException when role name already exists")
  void shouldThrowExceptionWhenRoleAlreadyExists() {
    var command = new CreateRoleCommand("admin", "Administrador");

    when(roleRepository.existsByName("ADMIN")).thenReturn(true);

    assertThrows(RoleAlreadyExistsException.class, () -> useCase.execute(command));

    verify(roleRepository, never()).save(any());
  }
}