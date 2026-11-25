package com.joaovtmarques.auth.infra.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joaovtmarques.auth.domain.role.exception.LastMasterAdminRemovalException;
import com.joaovtmarques.auth.domain.role.exception.RoleAlreadyExistsException;
import com.joaovtmarques.auth.domain.role.model.Role;
import com.joaovtmarques.auth.domain.role.usecase.assign.AssignRoleToUserUseCase;
import com.joaovtmarques.auth.domain.role.usecase.create.CreateRoleUseCase;
import com.joaovtmarques.auth.domain.role.usecase.list.ListRolesUseCase;
import com.joaovtmarques.auth.domain.role.usecase.remove.RemoveRoleFromUserUseCase;
import com.joaovtmarques.auth.domain.user.repository.UserRepository;
import com.joaovtmarques.auth.infra.security.SecurityConfigurations;
import com.joaovtmarques.auth.infra.security.token.JwtTokenService;
import com.joaovtmarques.auth.infra.web.dto.AssignRoleRequest;
import com.joaovtmarques.auth.infra.web.dto.CreateRoleRequest;
import com.joaovtmarques.auth.infra.web.exception.GlobalExceptionHandler;

@WebMvcTest(RoleController.class)
@Import({ SecurityConfigurations.class, GlobalExceptionHandler.class })
class RoleControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private CreateRoleUseCase createRoleUseCase;

  @MockitoBean
  private ListRolesUseCase listRolesUseCase;

  @MockitoBean
  private AssignRoleToUserUseCase assignRoleToUserUseCase;

  @MockitoBean
  private RemoveRoleFromUserUseCase removeRoleFromUserUseCase;

  @MockitoBean
  private JwtTokenService jwtTokenService;

  @MockitoBean
  private UserRepository userRepository;

  @Test
  @WithMockUser(roles = "AUTH_MASTER")
  @DisplayName("POST /roles - Should create role and return 201 when user is AUTH_MASTER")
  void shouldCreateRoleWhenMaster() throws Exception {
    UUID roleId = UUID.randomUUID();
    var request = new CreateRoleRequest("SARGENTEANTE", "Perfil de sargenteante");
    var createdRole = new Role(roleId, "SARGENTEANTE", "Perfil de sargenteante", Instant.now());

    when(createRoleUseCase.execute(any())).thenReturn(createdRole);

    mockMvc.perform(post("/roles")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(roleId.toString()))
        .andExpect(jsonPath("$.name").value("SARGENTEANTE"))
        .andExpect(jsonPath("$.description").value("Perfil de sargenteante"));
  }

  @Test
  @WithMockUser(roles = "COMUM")
  @DisplayName("POST /roles - Should return 403 Forbidden when user does not have AUTH_MASTER role")
  void shouldForbidNonMasterUser() throws Exception {
    var request = new CreateRoleRequest("ADMIN", "Administrador");

    mockMvc.perform(post("/roles")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden());

    verifyNoInteractions(createRoleUseCase);
  }

  @Test
  @WithMockUser(roles = "AUTH_MASTER")
  @DisplayName("POST /roles - Should return 409 Conflict when role already exists")
  void shouldReturnConflictWhenRoleAlreadyExists() throws Exception {
    var request = new CreateRoleRequest("ADMIN", "Administrador");

    when(createRoleUseCase.execute(any()))
        .thenThrow(new RoleAlreadyExistsException("Role with name ADMIN already exists."));

    mockMvc.perform(post("/roles")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("Role with name ADMIN already exists."));
  }

  @Test
  @WithMockUser(roles = "AUTH_MASTER")
  @DisplayName("GET /roles - Should list all roles and return 200")
  void shouldListAllRoles() throws Exception {
    var roles = List.of(
        new Role(UUID.randomUUID(), "ADMIN", "Admin", Instant.now()),
        new Role(UUID.randomUUID(), "COMUM", "Comum", Instant.now()));

    when(listRolesUseCase.listRoles()).thenReturn(roles);

    mockMvc.perform(get("/roles"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].name").value("ADMIN"))
        .andExpect(jsonPath("$[1].name").value("COMUM"));
  }

  @Test
  @WithMockUser(roles = "AUTH_MASTER")
  @DisplayName("POST /users/{userId}/roles - Should assign role and return 204")
  void shouldAssignRoleToUser() throws Exception {
    UUID userId = UUID.randomUUID();
    UUID roleId = UUID.randomUUID();
    var request = new AssignRoleRequest(roleId);

    mockMvc.perform(post("/users/{userId}/roles", userId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNoContent());

    verify(assignRoleToUserUseCase).assignRoleToUser(any());
  }

  @Test
  @WithMockUser(roles = "AUTH_MASTER")
  @DisplayName("DELETE /users/{userId}/roles/{roleId} - Should remove role and return 204")
  void shouldRemoveRoleFromUser() throws Exception {
    UUID userId = UUID.randomUUID();
    UUID roleId = UUID.randomUUID();

    mockMvc.perform(delete("/users/{userId}/roles/{roleId}", userId, roleId))
        .andExpect(status().isNoContent());

    verify(removeRoleFromUserUseCase).removeRoleFromUser(any());
  }

  @Test
  @WithMockUser(roles = "AUTH_MASTER")
  @DisplayName("DELETE /users/{userId}/roles/{roleId} - Should return 409 when removing last AUTH_MASTER")
  void shouldReturnConflictWhenRemovingLastMaster() throws Exception {
    UUID userId = UUID.randomUUID();
    UUID roleId = UUID.randomUUID();

    doThrow(new LastMasterAdminRemovalException("Cannot remove the last AUTH_MASTER role from the system."))
        .when(removeRoleFromUserUseCase).removeRoleFromUser(any());

    mockMvc.perform(delete("/users/{userId}/roles/{roleId}", userId, roleId))
        .andExpect(status().isConflict());
  }
}