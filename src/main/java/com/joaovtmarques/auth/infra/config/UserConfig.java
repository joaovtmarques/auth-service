package com.joaovtmarques.auth.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.joaovtmarques.auth.domain.user.repository.UserRepository;
import com.joaovtmarques.auth.domain.user.service.PasswordEncryptor;
import com.joaovtmarques.auth.domain.user.usecase.create.CreateUserUseCase;
import com.joaovtmarques.auth.domain.user.usecase.create.CreateUserUseCaseImpl;

@Configuration
public class UserConfig {

  @Bean
  public CreateUserUseCase createUserUseCase(UserRepository userRepository, PasswordEncryptor passwordEncryptor) {
    return new CreateUserUseCaseImpl(userRepository, passwordEncryptor);
  }
}