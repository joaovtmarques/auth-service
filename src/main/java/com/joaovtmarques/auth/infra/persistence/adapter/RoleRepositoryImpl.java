package com.joaovtmarques.auth.infra.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.joaovtmarques.auth.domain.role.model.Role;
import com.joaovtmarques.auth.domain.role.repository.RoleRepository;
import com.joaovtmarques.auth.infra.persistence.entity.RoleEntity;
import com.joaovtmarques.auth.infra.persistence.repository.SpringDataRoleRepository;

@Component
public class RoleRepositoryImpl implements RoleRepository {

  private final SpringDataRoleRepository springDataRoleRepository;

  public RoleRepositoryImpl(SpringDataRoleRepository springDataRoleRepository) {
    this.springDataRoleRepository = springDataRoleRepository;
  }

  @Override
  public Role save(Role role) {
    RoleEntity entity = toEntity(role);
    RoleEntity savedEntity = springDataRoleRepository.save(entity);
    return toDomain(savedEntity);
  }

  @Override
  public Optional<Role> findByName(String name) {
    return springDataRoleRepository.findByName(name)
        .map(this::toDomain);
  }

  @Override
  public Optional<Role> findById(UUID id) {
    return springDataRoleRepository.findById(id)
        .map(this::toDomain);
  }

  @Override
  public List<Role> findAll() {
    return springDataRoleRepository.findAll().stream()
        .map(this::toDomain)
        .toList();
  }

  @Override
  public boolean existsByName(String name) {
    return springDataRoleRepository.existsByName(name);
  }

  private RoleEntity toEntity(Role role) {
    return new RoleEntity(
        role.getId(),
        role.getName(),
        role.getDescription(),
        role.getCreatedAt());
  }

  private Role toDomain(RoleEntity entity) {
    return new Role(
        entity.getId(),
        entity.getName(),
        entity.getDescription(),
        entity.getCreatedAt());
  }
}