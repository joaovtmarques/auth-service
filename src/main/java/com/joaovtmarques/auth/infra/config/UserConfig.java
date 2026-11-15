package com.joaovtmarques.auth.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.joaovtmarques.auth.domain.common.service.HashService;
import com.joaovtmarques.auth.domain.refresh_token.repository.RefreshTokenRepository;
import com.joaovtmarques.auth.domain.refresh_token.usecase.create.CreateRefreshTokenUseCase;
import com.joaovtmarques.auth.domain.refresh_token.usecase.create.CreateRefreshTokenUseCaseImpl;
import com.joaovtmarques.auth.domain.refresh_token.usecase.revoke.RevokeRefreshTokenUseCase;
import com.joaovtmarques.auth.domain.refresh_token.usecase.revoke.RevokeRefreshTokenUseCaseImpl;
import com.joaovtmarques.auth.domain.refresh_token.usecase.revoke_all.RevokeAllRefreshTokensUseCase;
import com.joaovtmarques.auth.domain.refresh_token.usecase.revoke_all.RevokeAllRefreshTokensUseCaseImpl;
import com.joaovtmarques.auth.domain.refresh_token.usecase.rotate.RotateRefreshTokenUseCase;
import com.joaovtmarques.auth.domain.refresh_token.usecase.rotate.RotateRefreshTokenUseCaseImpl;
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
  CreateUserUseCase createUserUseCase(
      UserRepository userRepository,
      PasswordEncryptor passwordEncryptor) {
    return new CreateUserUseCaseImpl(userRepository, passwordEncryptor);
  }

  @Bean
  AuthenticateUserUseCase authenticateUserUseCase(
      UserRepository userRepository,
      PasswordEncryptor passwordEncryptor,
      TokenService tokenService) {
    return new AuthenticateUserUseCaseImpl(userRepository, passwordEncryptor, tokenService);
  }

  @Bean
  CreateRefreshTokenUseCase createRefreshTokenUseCase(
      RefreshTokenRepository refreshTokenRepository,
      HashService hashService) {
    return new CreateRefreshTokenUseCaseImpl(refreshTokenRepository, hashService);
  }

  @Bean
  RotateRefreshTokenUseCase rotateRefreshTokenUseCase(
      RefreshTokenRepository refreshTokenRepository,
      HashService hashService,
      TokenService tokenService,
      CreateRefreshTokenUseCase createRefreshTokenUseCase) {
    return new RotateRefreshTokenUseCaseImpl(
        refreshTokenRepository,
        hashService,
        tokenService,
        createRefreshTokenUseCase);
  }

  @Bean
  RevokeRefreshTokenUseCase revokeRefreshTokenUseCase(
      RefreshTokenRepository refreshTokenRepository,
      HashService hashService) {
    return new RevokeRefreshTokenUseCaseImpl(refreshTokenRepository, hashService);
  }

  @Bean
  RevokeAllRefreshTokensUseCase revokeAllRefreshTokensUseCase(
      RefreshTokenRepository refreshTokenRepository) {
    return new RevokeAllRefreshTokensUseCaseImpl(refreshTokenRepository);
  }
}