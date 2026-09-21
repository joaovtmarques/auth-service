package com.joaovtmarques.auth.domain.refresh_token.usecase.create;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import com.joaovtmarques.auth.domain.common.service.HashService;
import com.joaovtmarques.auth.domain.refresh_token.model.RefreshToken;
import com.joaovtmarques.auth.domain.refresh_token.repository.RefreshTokenRepository;

public class CreateRefreshTokenUseCaseImpl implements CreateRefreshTokenUseCase {

  private final RefreshTokenRepository refreshTokenRepository;
  private final HashService hashService;

  public CreateRefreshTokenUseCaseImpl(RefreshTokenRepository refreshTokenRepository, HashService hashService) {
    this.refreshTokenRepository = refreshTokenRepository;
    this.hashService = hashService;
  }

  @Override
  public String execute(CreateRefreshTokenCommand command) {
    String token = UUID.randomUUID().toString();
    String tokenHash = hashService.hash(token);
    Instant expiresAt = Instant.now().plus(7, ChronoUnit.DAYS);
    RefreshToken refreshToken = command.toModel(tokenHash, expiresAt);
    refreshTokenRepository.save(refreshToken);

    return token;
  }
}