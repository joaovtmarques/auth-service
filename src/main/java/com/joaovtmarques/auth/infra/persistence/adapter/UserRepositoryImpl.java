package com.joaovtmarques.auth.infra.persistence.adapter;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.joaovtmarques.auth.domain.user.model.User;
import com.joaovtmarques.auth.domain.user.repository.UserRepository;
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

  private UserEntity toEntity(User user) {
    return new UserEntity(
        user.getId(),
        user.getName(),
        user.getEmail(),
        user.getPhone(),
        user.getPasswordHash(),
        user.getCreatedAt(),
        user.getUpdatedAt());
  }

  private User toDomain(UserEntity entity) {
    return new User(
        entity.getId(),
        entity.getName(),
        entity.getEmail(),
        entity.getPhone(),
        entity.getPasswordHash(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}