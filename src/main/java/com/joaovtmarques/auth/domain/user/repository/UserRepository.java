package com.joaovtmarques.auth.domain.user.repository;

import java.util.Optional;
import java.util.UUID;

import com.joaovtmarques.auth.domain.user.model.User;

public interface UserRepository {

  User save(User user);

  Optional<User> findByEmail(String email);

  Optional<User> findById(UUID userId);

}
