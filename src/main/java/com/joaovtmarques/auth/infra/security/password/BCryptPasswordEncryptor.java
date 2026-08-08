package com.joaovtmarques.auth.infra.security.password;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.joaovtmarques.auth.domain.user.service.PasswordEncryptor;

@Component
public class BCryptPasswordEncryptor implements PasswordEncryptor {

  private final BCryptPasswordEncoder passwordEncoder;

  public BCryptPasswordEncryptor() {
    this.passwordEncoder = new BCryptPasswordEncoder();
  }

  @Override
  public String encrypt(String rawPassword) {
    return passwordEncoder.encode(rawPassword);
  }

  @Override
  public boolean matches(String rawPassword, String encryptedPassword) {
    return passwordEncoder.matches(rawPassword, encryptedPassword);
  }
}