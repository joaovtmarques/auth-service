package com.joaovtmarques.auth.domain.user.usecase.authenticate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.joaovtmarques.auth.domain.user.exception.InvalidCredentialsException;
import com.joaovtmarques.auth.domain.user.exception.UserNotFoundException;
import com.joaovtmarques.auth.domain.user.model.User;
import com.joaovtmarques.auth.domain.user.repository.UserRepository;
import com.joaovtmarques.auth.domain.user.service.PasswordEncryptor;
import com.joaovtmarques.auth.domain.user.service.TokenService;

@ExtendWith(MockitoExtension.class)
class AuthenticateUserUseCaseTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncryptor passwordEncryptor;

  @Mock
  private TokenService tokenService;

  private AuthenticateUserUseCaseImpl authenticateUserUseCase;

  @BeforeEach
  void setUp() {
    authenticateUserUseCase = new AuthenticateUserUseCaseImpl(userRepository, passwordEncryptor, tokenService);
  }

  @Test
  @DisplayName("Should authenticate a user successfully")
  void sholdAuthenticateUserSuccessfully() {

    // Arrange
    AuthenticateUserCommand command = new AuthenticateUserCommand("example@email.com", "12345678");

    User user = new User(new UUID(1, 1), "testname", "110000000000", "example@email.com", "hashed_password");

    when(userRepository.findByEmail("example@email.com")).thenReturn(Optional.of(user));
    when(passwordEncryptor.matches("12345678", "hashed_password")).thenReturn(true);
    when(tokenService.generateToken(user.getId().toString())).thenReturn("valid_jwt_token");

    // Act
    String result = authenticateUserUseCase.execute(command);

    // Assert
    assertNotNull(result);
    assertEquals(result, "valid_jwt_token");
    verify(passwordEncryptor, times(1)).matches("12345678", "hashed_password");
    verify(userRepository, times(1)).findByEmail("example@email.com");
    verify(tokenService, times(1)).generateToken(user.getId().toString());
  }

  @Test
  @DisplayName("Should throw UserNotFoundException when user is not found by email")
  void shouldThrowExceptionWhenUserNotFound() {
    // Arrange
    AuthenticateUserCommand command = new AuthenticateUserCommand("nonexistent@email.com", "12345678");

    when(userRepository.findByEmail("nonexistent@email.com")).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(UserNotFoundException.class, () -> {
      authenticateUserUseCase.execute(command);
    });

    // Verificações: não deve tentar comparar senha nem gerar token se o usuário não
    // existe
    verify(userRepository, times(1)).findByEmail("nonexistent@email.com");
    verifyNoInteractions(passwordEncryptor);
    verifyNoInteractions(tokenService);
  }

  @Test
  @DisplayName("Should throw InvalidCredentialsException when password does not match")
  void shouldThrowExceptionWhenPasswordIsIncorrect() {
    // Arrange
    AuthenticateUserCommand command = new AuthenticateUserCommand("example@email.com", "wrong_password");
    User user = new User(UUID.randomUUID(), "testname", "110000000000", "example@email.com", "hashed_password");

    when(userRepository.findByEmail("example@email.com")).thenReturn(Optional.of(user));
    when(passwordEncryptor.matches("wrong_password", "hashed_password")).thenReturn(false);

    // Act & Assert
    assertThrows(InvalidCredentialsException.class, () -> {
      authenticateUserUseCase.execute(command);
    });

    // Verificações: buscou o usuário, validou a senha, mas NÃO deve gerar o token
    verify(userRepository, times(1)).findByEmail("example@email.com");
    verify(passwordEncryptor, times(1)).matches("wrong_password", "hashed_password");
    verifyNoInteractions(tokenService);
  }

}
