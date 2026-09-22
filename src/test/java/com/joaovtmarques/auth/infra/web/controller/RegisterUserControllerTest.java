package com.joaovtmarques.auth.infra.web.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joaovtmarques.auth.domain.user.exception.UserAlreadyExistsException;
import com.joaovtmarques.auth.domain.user.repository.UserRepository;
import com.joaovtmarques.auth.domain.user.usecase.create.CreateUserCommand;
import com.joaovtmarques.auth.domain.user.usecase.create.CreateUserUseCase;
import com.joaovtmarques.auth.infra.security.token.JwtTokenService;
import com.joaovtmarques.auth.infra.web.dto.RegisterUserRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegisterUserController.class)
@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings("null")
class RegisterUserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private CreateUserUseCase createUserUseCase;

  @MockitoBean
  private UserRepository userRepository;

  @MockitoBean
  private JwtTokenService jwtTokenService;

  @Test
  @DisplayName("Should return 201 Created when user payload is valid")
  void shouldRegisterUserSuccessfully() throws Exception {
    RegisterUserRequest request = new RegisterUserRequest(
        "João Silva",
        "joao@email.com",
        "11999999999",
        "Password123!");

    mockMvc.perform(post("/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());

    verify(createUserUseCase).execute(any(CreateUserCommand.class));
  }

  @Test
  @DisplayName("Should return 400 Bad Request when validation annotations fail")
  void shouldReturn400WhenPayloadIsInvalid() throws Exception {
    RegisterUserRequest invalidRequest = new RegisterUserRequest(
        "",
        "invalid-email-format",
        "",
        "123");

    mockMvc.perform(post("/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should propagate domain exception when user email already exists")
  void shouldReturnErrorWhenUserAlreadyExists() throws Exception {
    RegisterUserRequest request = new RegisterUserRequest(
        "João Silva",
        "joao@email.com",
        "11999999999",
        "Password123!");

    doThrow(new UserAlreadyExistsException("Email already registered: joao@email.com"))
        .when(createUserUseCase).execute(any(CreateUserCommand.class));

    mockMvc.perform(post("/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict());
  }
}