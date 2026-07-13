package com.joaovtmarques.auth.domain.user.usecase.create;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.joaovtmarques.auth.domain.user.model.User;
import com.joaovtmarques.auth.domain.user.repository.UserRepository;
import com.joaovtmarques.auth.domain.user.service.PasswordEncryptor;

@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncryptor passwordEncryptor;

  private CreateUserUseCaseImpl createUserUseCase;

  @BeforeEach
  void setUp() {
    createUserUseCase = new CreateUserUseCaseImpl(userRepository, passwordEncryptor);
  }

  @Test
  @DisplayName("Should create a user successfully")
  void sholdCreateUserSuccessfully() {
    // Arrange
    CreateUserCommand command = new CreateUserCommand("João", "joao@email.com", "110000000000", "12345678");

    String simulatedHash = "simulatedHash";

    when(passwordEncryptor.encrypt(command.password())).thenReturn(simulatedHash);
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    User result = createUserUseCase.execute(command);

    // Assert
    assertNotNull(result);
    assertEquals(command.name(), result.getName());
    assertEquals(command.email(), result.getEmail());
    assertEquals(command.phone(), result.getPhone());
    assertEquals(simulatedHash, result.getPasswordHash());

    verify(passwordEncryptor, times(1)).encrypt(command.password());
    verify(userRepository, times(1)).save(any(User.class));
  }

}
