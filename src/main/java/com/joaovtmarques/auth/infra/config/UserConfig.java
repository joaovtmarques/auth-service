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
import com.joaovtmarques.auth.domain.role.repository.RoleRepository;
import com.joaovtmarques.auth.domain.role.usecase.assign.AssignRoleToUserUseCase;
import com.joaovtmarques.auth.domain.role.usecase.assign.AssignRoleToUserUseCaseImpl;
import com.joaovtmarques.auth.domain.role.usecase.create.CreateRoleUseCase;
import com.joaovtmarques.auth.domain.role.usecase.create.CreateRoleUseCaseImpl;
import com.joaovtmarques.auth.domain.role.usecase.list.ListRolesUseCase;
import com.joaovtmarques.auth.domain.role.usecase.list.ListRolesUseCaseImpl;
import com.joaovtmarques.auth.domain.role.usecase.remove.RemoveRoleFromUserUseCase;
import com.joaovtmarques.auth.domain.role.usecase.remove.RemoveRoleFromUserUseCaseImpl;
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
      UserRepository userRepository,
      HashService hashService,
      TokenService tokenService,
      CreateRefreshTokenUseCase createRefreshTokenUseCase) {
    return new RotateRefreshTokenUseCaseImpl(
        refreshTokenRepository,
        userRepository,
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

  @Bean
  public CreateRoleUseCase createRoleUseCase(RoleRepository roleRepository) {
    return new CreateRoleUseCaseImpl(roleRepository);
  }

  @Bean
  public ListRolesUseCase listRolesUseCase(RoleRepository roleRepository) {
    return new ListRolesUseCaseImpl(roleRepository);
  }

  @Bean
  AssignRoleToUserUseCase assignRoleToUserUseCase(
      UserRepository userRepository, RoleRepository roleRepository) {
    return new AssignRoleToUserUseCaseImpl(roleRepository, userRepository);
  }

  @Bean
  RemoveRoleFromUserUseCase removeRoleFromUserUseCase(UserRepository userRepository, RoleRepository roleRepository) {
    return new RemoveRoleFromUserUseCaseImpl(roleRepository, userRepository);
  }
}