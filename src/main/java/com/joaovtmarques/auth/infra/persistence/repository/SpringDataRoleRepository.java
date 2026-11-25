package com.joaovtmarques.auth.infra.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.joaovtmarques.auth.infra.persistence.entity.RoleEntity;

public interface SpringDataRoleRepository extends JpaRepository<RoleEntity, UUID> {
  Optional<RoleEntity> findByName(String name);

  boolean existsByName(String name);
}