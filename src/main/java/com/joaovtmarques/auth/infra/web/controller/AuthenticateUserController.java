package com.joaovtmarques.auth.infra.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joaovtmarques.auth.domain.user.usecase.authenticate.AuthenticateUserCommand;
import com.joaovtmarques.auth.domain.user.usecase.authenticate.AuthenticateUserUseCase;
import com.joaovtmarques.auth.infra.web.dto.AuthResponse;
import com.joaovtmarques.auth.infra.web.dto.AuthenticateUserRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthenticateUserController {

  private final AuthenticateUserUseCase authenticateUserUseCase;

  public AuthenticateUserController(AuthenticateUserUseCase authenticateUserUseCase) {
    this.authenticateUserUseCase = authenticateUserUseCase;
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> handle(@RequestBody @Valid AuthenticateUserRequest request) {
    AuthenticateUserCommand command = new AuthenticateUserCommand(
        request.email(),
        request.password());

    String token = authenticateUserUseCase.execute(command);
    return ResponseEntity.ok(new AuthResponse(token));
  }
}