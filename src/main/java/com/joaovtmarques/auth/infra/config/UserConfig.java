package com.joaovtmarques.auth.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.joaovtmarques.auth.domain.user.repository.UserRepository;
import com.joaovtmarques.auth.domain.user.service.PasswordEncryptor;
import com.joaovtmarques.auth.domain.user.service.TokenService;
import com.joaovtmarques.auth.domain.user.usecase.authenticate.AuthenticateUserUseCase;
import com.joaovtmarques.auth.domain.user.usecase.authenticate.AuthenticateUserUseCaseImpl;
import com.joaovtmarques.auth.domain.user.usecase.create.CreateUserUseCase;
import com.joaovtmarques.auth.domain.user.usecase.create.CreateUserUseCaseImpl;

@Configuration
public class UserConfig {

  @Bean
  public CreateUserUseCase createUserUseCase(
      UserRepository userRepository,
      PasswordEncryptor passwordEncryptor) {
    return new CreateUserUseCaseImpl(userRepository, passwordEncryptor);
  }

  @Bean
  public AuthenticateUserUseCase authenticateUserUseCase(
      UserRepository userRepository,
      PasswordEncryptor passwordEncryptor,
      TokenService tokenService) {
    return new AuthenticateUserUseCaseImpl(userRepository, passwordEncryptor, tokenService);
  }
}