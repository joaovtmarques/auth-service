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
import com.joaovtmarques.auth.domain.user.exception.InvalidCredentialsException;
import com.joaovtmarques.auth.domain.user.repository.UserRepository;
import com.joaovtmarques.auth.domain.user.usecase.authenticate.AuthenticateUserCommand;
import com.joaovtmarques.auth.domain.user.usecase.authenticate.AuthenticateUserUseCase;
import com.joaovtmarques.auth.infra.security.token.JwtTokenService;
import com.joaovtmarques.auth.infra.web.dto.AuthenticateUserRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticateUserController.class)
@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings("null")
class AuthenticateUserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private JwtTokenService jwtTokenService;

  @MockitoBean
  private UserRepository userRepository;

  @MockitoBean
  private AuthenticateUserUseCase authenticateUserUseCase;

  @Test
  @DisplayName("Should return 200 OK and token with type Bearer when credentials are valid")
  void shouldAuthenticateUserSuccessfully() throws Exception {
    AuthenticateUserRequest request = new AuthenticateUserRequest("joao@email.com", "Password123!");
    String mockJwtToken = "mocked.jwt.token";

    when(authenticateUserUseCase.execute(any(AuthenticateUserCommand.class)))
        .thenReturn(mockJwtToken);

    mockMvc.perform(post("/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value(mockJwtToken))
        .andExpect(jsonPath("$.type").value("Bearer"));
  }

  @Test
  @DisplayName("Should return 400 Bad Request when login request body is invalid")
  void shouldReturn400WhenLoginPayloadIsInvalid() throws Exception {
    AuthenticateUserRequest invalidRequest = new AuthenticateUserRequest("email-invalido", "");

    mockMvc.perform(post("/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should propagate InvalidCredentialsException when credentials do not match")
  void shouldReturnUnauthorizedWhenCredentialsAreInvalid() throws Exception {
    AuthenticateUserRequest request = new AuthenticateUserRequest("joao@email.com", "WrongPassword");

    when(authenticateUserUseCase.execute(any(AuthenticateUserCommand.class)))
        .thenThrow(new InvalidCredentialsException("Invalid e-mail or password"));

    mockMvc.perform(post("/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized()); // Ou o status capturado pelo seu GlobalExceptionHandler
  }
}