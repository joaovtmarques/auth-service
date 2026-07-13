package com.joaovtmarques.auth.domain.user.usecase.authenticate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

}
