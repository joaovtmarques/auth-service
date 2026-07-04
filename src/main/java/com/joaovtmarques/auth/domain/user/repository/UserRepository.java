package com.joaovtmarques.auth.domain.user.repository;

import com.joaovtmarques.auth.domain.user.model.User;

public interface UserRepository {

  User save(User user);

}
