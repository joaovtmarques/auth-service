package com.joaovtmarques.auth.domain.role.usecase.list;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.joaovtmarques.auth.domain.role.model.Role;
import com.joaovtmarques.auth.domain.role.repository.RoleRepository;

@ExtendWith(MockitoExtension.class)
class ListRolesUseCaseImplTest {

  @Mock
  private RoleRepository roleRepository;

  @InjectMocks
  private ListRolesUseCaseImpl useCase;

  @Test
  @DisplayName("Should return all registered roles")
  void shouldReturnAllRoles() {
    var roles = List.of(
        new Role(UUID.randomUUID(), "ADMIN", "Admin role", Instant.now()),
        new Role(UUID.randomUUID(), "SARGENTEANTE", "Sargenteante role", Instant.now()),
        new Role(UUID.randomUUID(), "COMUM", "Basic user role", Instant.now()));

    when(roleRepository.findAll()).thenReturn(roles);

    List<Role> result = useCase.listRoles();

    assertEquals(3, result.size());
    verify(roleRepository).findAll();
  }
}