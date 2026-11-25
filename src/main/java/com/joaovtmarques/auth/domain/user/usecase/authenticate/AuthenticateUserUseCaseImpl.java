package com.joaovtmarques.auth.domain.user.usecase.authenticate;

import java.util.List;

import com.joaovtmarques.auth.domain.role.model.Role;
import com.joaovtmarques.auth.domain.user.exception.InvalidCredentialsException;
import com.joaovtmarques.auth.domain.user.exception.UserNotFoundException;
import com.joaovtmarques.auth.domain.user.model.User;
import com.joaovtmarques.auth.domain.user.repository.UserRepository;
import com.joaovtmarques.auth.domain.user.service.PasswordEncryptor;
import com.joaovtmarques.auth.domain.user.service.TokenService;

public class AuthenticateUserUseCaseImpl implements AuthenticateUserUseCase {

  private final UserRepository userRepository;
  private final PasswordEncryptor passwordEncryptor;
  private final TokenService tokenService;

  public AuthenticateUserUseCaseImpl(
      UserRepository userRepository,
      PasswordEncryptor passwordEncryptor,
      TokenService tokenService) {
    this.userRepository = userRepository;
    this.passwordEncryptor = passwordEncryptor;
    this.tokenService = tokenService;
  }

  @Override
  public String execute(AuthenticateUserCommand command) {
    User user = userRepository.findByEmail(command.email())
        .orElseThrow(() -> new UserNotFoundException("User not found"));

    if (!passwordEncryptor.matches(command.password(), user.getPasswordHash())) {
      throw new InvalidCredentialsException("Invalid e-mail or password");
    }

    List<String> roles = user.getRoles().stream()
        .map(Role::getName)
        .toList();

    return tokenService.generateToken(user.getId().toString(), roles);
  }
}