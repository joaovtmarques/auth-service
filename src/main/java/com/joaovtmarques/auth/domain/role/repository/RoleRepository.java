package com.joaovtmarques.auth.domain.role.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.joaovtmarques.auth.domain.role.model.Role;

public interface RoleRepository {
  Role save(Role role);

  Optional<Role> findByName(String name);

  Optional<Role> findById(UUID id);

  List<Role> findAll();

  boolean existsByName(String name);
}