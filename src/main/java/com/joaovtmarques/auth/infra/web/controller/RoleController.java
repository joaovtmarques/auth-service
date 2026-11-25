package com.joaovtmarques.auth.infra.web.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.joaovtmarques.auth.domain.role.usecase.assign.AssignRoleCommand;
import com.joaovtmarques.auth.domain.role.usecase.assign.AssignRoleToUserUseCase;
import com.joaovtmarques.auth.domain.role.usecase.create.CreateRoleCommand;
import com.joaovtmarques.auth.domain.role.usecase.create.CreateRoleUseCase;
import com.joaovtmarques.auth.domain.role.usecase.list.ListRolesUseCase;
import com.joaovtmarques.auth.domain.role.usecase.remove.RemoveRoleCommand;
import com.joaovtmarques.auth.domain.role.usecase.remove.RemoveRoleFromUserUseCase;
import com.joaovtmarques.auth.infra.web.dto.AssignRoleRequest;
import com.joaovtmarques.auth.infra.web.dto.CreateRoleRequest;
import com.joaovtmarques.auth.infra.web.dto.RoleResponse;

import jakarta.validation.Valid;

@RestController
@PreAuthorize("hasRole('AUTH_MASTER')")
public class RoleController {

  private final CreateRoleUseCase createRoleUseCase;
  private final ListRolesUseCase listRolesUseCase;
  private final AssignRoleToUserUseCase assignRoleToUserUseCase;
  private final RemoveRoleFromUserUseCase removeRoleFromUserUseCase;

  public RoleController(
      CreateRoleUseCase createRoleUseCase,
      ListRolesUseCase listRolesUseCase,
      AssignRoleToUserUseCase assignRoleToUserUseCase,
      RemoveRoleFromUserUseCase removeRoleFromUserUseCase) {
    this.createRoleUseCase = createRoleUseCase;
    this.listRolesUseCase = listRolesUseCase;
    this.assignRoleToUserUseCase = assignRoleToUserUseCase;
    this.removeRoleFromUserUseCase = removeRoleFromUserUseCase;
  }

  @PostMapping("/roles")
  public ResponseEntity<RoleResponse> create(@RequestBody @Valid CreateRoleRequest request) {
    var command = new CreateRoleCommand(request.name(), request.description());
    var createdRole = createRoleUseCase.execute(command);
    return ResponseEntity.status(HttpStatus.CREATED).body(RoleResponse.from(createdRole));
  }

  @GetMapping("/roles")
  public ResponseEntity<List<RoleResponse>> listAll() {
    var roles = listRolesUseCase.listRoles().stream()
        .map(RoleResponse::from)
        .toList();
    return ResponseEntity.ok(roles);
  }

  @PostMapping("/users/{userId}/roles")
  public ResponseEntity<Void> assignRole(
      @PathVariable UUID userId,
      @RequestBody @Valid AssignRoleRequest request) {
    var command = new AssignRoleCommand(userId, request.roleId());
    assignRoleToUserUseCase.assignRoleToUser(command);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/users/{userId}/roles/{roleId}")
  public ResponseEntity<Void> removeRole(
      @PathVariable UUID userId,
      @PathVariable UUID roleId) {
    var command = new RemoveRoleCommand(userId, roleId);
    removeRoleFromUserUseCase.removeRoleFromUser(command);
    return ResponseEntity.noContent().build();
  }
}