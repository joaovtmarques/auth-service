package com.joaovtmarques.auth.domain.user.usecase.authenticate;

import java.util.Optional;

import com.joaovtmarques.auth.domain.user.exception.InvalidCredentialsException;
import com.joaovtmarques.auth.domain.user.exception.UserNotFoundException;
import com.joaovtmarques.auth.domain.user.model.User;
import com.joaovtmarques.auth.domain.user.repository.UserRepository;
import com.joaovtmarques.auth.domain.user.service.TokenService;
import com.joaovtmarques.auth.domain.user.service.PasswordEncryptor;

public class AuthenticateUserUseCaseImpl implements AuthenticateUserUseCase {

  private final UserRepository userRepository;
  private final PasswordEncryptor passwordEncryptor;
  private final TokenService generateToken;

  public AuthenticateUserUseCaseImpl(UserRepository userRepository, PasswordEncryptor passwordEncryptor,
      TokenService generateToken) {
    this.userRepository = userRepository;
    this.passwordEncryptor = passwordEncryptor;
    this.generateToken = generateToken;
  }

  @Override
  public String execute(AuthenticateUserCommand command) {
    Optional<User> user = userRepository.findByEmail(command.email());
    if (!user.isPresent()) {
      throw new UserNotFoundException("User not found");
    }
    if (!passwordEncryptor.matches(command.password(), user.get().getPasswordHash())) {
      throw new InvalidCredentialsException("Invalid e-mail or password");
    }

    String token = generateToken.generateToken(user.get().getId().toString());

    return token;
  }

}
