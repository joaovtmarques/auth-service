package com.joaovtmarques.auth.domain.user.service;

public interface PasswordEncryptor {

  String encrypt(String rawPassword);

  boolean matches(String rawPassword, String hashedPassword);

}
