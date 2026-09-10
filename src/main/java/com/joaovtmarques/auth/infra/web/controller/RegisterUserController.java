package com.joaovtmarques.auth.infra.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joaovtmarques.auth.domain.user.usecase.create.CreateUserCommand;
import com.joaovtmarques.auth.domain.user.usecase.create.CreateUserUseCase;
import com.joaovtmarques.auth.infra.web.dto.RegisterUserRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class RegisterUserController {

  private final CreateUserUseCase createUserUseCase;

  public RegisterUserController(CreateUserUseCase createUserUseCase) {
    this.createUserUseCase = createUserUseCase;
  }

  @PostMapping("/register")
  public ResponseEntity<Void> handle(@RequestBody @Valid RegisterUserRequest request) {
    CreateUserCommand command = new CreateUserCommand(
        request.name(),
        request.email(),
        request.phone(),
        request.password());

    createUserUseCase.execute(command);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}