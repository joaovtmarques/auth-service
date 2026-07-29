package com.joaovtmarques.auth.domain.user.usecase.create;

import com.joaovtmarques.auth.domain.user.model.User;
import com.joaovtmarques.auth.domain.user.repository.UserRepository;
import com.joaovtmarques.auth.domain.user.service.PasswordEncryptor;
import com.joaovtmarques.auth.domain.user.exception.UserAlreadyExistsException; // Exceção de domínio

public class CreateUserUseCaseImpl implements CreateUserUseCase {

  private final UserRepository userRepository;
  private final PasswordEncryptor passwordEncryptor;

  public CreateUserUseCaseImpl(UserRepository userRepository, PasswordEncryptor passwordEncryptor) {
    this.userRepository = userRepository;
    this.passwordEncryptor = passwordEncryptor;
  }

  @Override
  public User execute(CreateUserCommand command) {
    if (userRepository.findByEmail(command.email()).isPresent()) {
      throw new UserAlreadyExistsException("Email already registered: " + command.email());
    }

    String encryptedPassword = passwordEncryptor.encrypt(command.password());
    User user = command.toModel(encryptedPassword);
    return userRepository.save(user);
  }
}