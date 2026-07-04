package com.joaovtmarques.auth.domain.user.usecase.create;

import com.joaovtmarques.auth.domain.user.model.User;

public record CreateUserCommand(
    String name,
    String email,
    String phone,
    String password) {

  public User toModel(String encryptedPassword) {
    User user = new User();
    user.setName(name);
    user.setEmail(email);
    user.setPhone(phone);
    user.setPasswordHash(encryptedPassword);
    return user;
  }

}
