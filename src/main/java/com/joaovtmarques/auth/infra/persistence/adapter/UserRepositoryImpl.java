package com.joaovtmarques.auth.infra.persistence.adapter;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.joaovtmarques.auth.domain.role.model.Role;
import com.joaovtmarques.auth.domain.user.model.User;
import com.joaovtmarques.auth.domain.user.repository.UserRepository;
import com.joaovtmarques.auth.infra.persistence.entity.RoleEntity;
import com.joaovtmarques.auth.infra.persistence.entity.UserEntity;
import com.joaovtmarques.auth.infra.persistence.repository.SpringDataUserRepository;

@Component
public class UserRepositoryImpl implements UserRepository {

    private final SpringDataUserRepository springDataUserRepository;

    public UserRepositoryImpl(SpringDataUserRepository springDataUserRepository) {
        this.springDataUserRepository = springDataUserRepository;
    }

    @Override
    public User save(User user) {
        UserEntity entity = toEntity(user);
        UserEntity savedEntity = springDataUserRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return springDataUserRepository.findByEmail(email)
                .map(this::toDomain);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return springDataUserRepository.findById(id)
                .map(this::toDomain);
    }

    private UserEntity toEntity(User user) {
        Set<RoleEntity> roleEntities = user.getRoles() != null
                ? user.getRoles().stream()
                        .map(role -> new RoleEntity(
                                role.getId(),
                                role.getName(),
                                role.getDescription(),
                                role.getCreatedAt()))
                        .collect(Collectors.toSet())
                : new HashSet<>();

        return new UserEntity(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getPasswordHash(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                roleEntities);
    }

    private User toDomain(UserEntity entity) {
        Set<Role> roles = entity.getRoles() != null
                ? entity.getRoles().stream()
                        .map(roleEntity -> new Role(
                                roleEntity.getId(),
                                roleEntity.getName(),
                                roleEntity.getDescription(),
                                roleEntity.getCreatedAt()))
                        .collect(Collectors.toSet())
                : new HashSet<>();

        User user = new User(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getPasswordHash(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
        user.setRoles(roles);

        return user;
    }

    @Override
    public long countByRoleName(String roleName) {
        return springDataUserRepository.countByRolesName(roleName);
    }
}