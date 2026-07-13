package com.joaovtmarques.auth.domain.user.repository;

import java.util.Optional;

import com.joaovtmarques.auth.domain.user.model.User;

public interface UserRepository {

  User save(User user);

  Optional<User> findByEmail(String email);

}
