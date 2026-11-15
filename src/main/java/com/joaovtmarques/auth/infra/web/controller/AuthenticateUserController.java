package com.joaovtmarques.auth.infra.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joaovtmarques.auth.domain.refresh_token.usecase.revoke.RevokeRefreshTokenCommand;
import com.joaovtmarques.auth.domain.refresh_token.usecase.revoke.RevokeRefreshTokenUseCase;
import com.joaovtmarques.auth.domain.refresh_token.usecase.rotate.RotateRefreshTokenCommand;
import com.joaovtmarques.auth.domain.refresh_token.usecase.rotate.RotateRefreshTokenResult;
import com.joaovtmarques.auth.domain.refresh_token.usecase.rotate.RotateRefreshTokenUseCase;
import com.joaovtmarques.auth.domain.user.usecase.authenticate.AuthenticateUserCommand;
import com.joaovtmarques.auth.domain.user.usecase.authenticate.AuthenticateUserUseCase;
import com.joaovtmarques.auth.infra.web.dto.AuthResponse;
import com.joaovtmarques.auth.infra.web.dto.AuthenticateUserRequest;
import com.joaovtmarques.auth.infra.web.dto.RefreshTokenRequest;
import com.joaovtmarques.auth.infra.web.dto.RefreshTokenResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthenticateUserController {

  private final AuthenticateUserUseCase authenticateUserUseCase;
  private final RotateRefreshTokenUseCase rotateRefreshTokenUseCase;
  private final RevokeRefreshTokenUseCase revokeRefreshTokenUseCase;

  public AuthenticateUserController(AuthenticateUserUseCase authenticateUserUseCase,
      RotateRefreshTokenUseCase rotateRefreshTokenUseCase, RevokeRefreshTokenUseCase revokeRefreshTokenUseCase) {
    this.authenticateUserUseCase = authenticateUserUseCase;
    this.rotateRefreshTokenUseCase = rotateRefreshTokenUseCase;
    this.revokeRefreshTokenUseCase = revokeRefreshTokenUseCase;
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> handle(@RequestBody @Valid AuthenticateUserRequest request) {
    AuthenticateUserCommand command = new AuthenticateUserCommand(
        request.email(),
        request.password());

    String token = authenticateUserUseCase.execute(command);
    return ResponseEntity.ok(new AuthResponse(token));
  }

  @PostMapping("/refresh")
  public ResponseEntity<RefreshTokenResponse> refresh(
      @RequestBody @Valid RefreshTokenRequest body,
      HttpServletRequest httpRequest) {

    String ipAddress = httpRequest.getRemoteAddr();
    String userAgent = httpRequest.getHeader("User-Agent");

    RotateRefreshTokenCommand command = new RotateRefreshTokenCommand(
        body.refreshToken(),
        ipAddress,
        userAgent);

    RotateRefreshTokenResult result = rotateRefreshTokenUseCase.execute(command);

    return ResponseEntity.ok(new RefreshTokenResponse(
        result.accessToken(),
        result.refreshToken()));
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(@RequestBody @Valid RefreshTokenRequest body) {
    revokeRefreshTokenUseCase.execute(new RevokeRefreshTokenCommand(body.refreshToken()));
    return ResponseEntity.noContent().build();
  }

}