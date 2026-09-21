package com.joaovtmarques.auth.domain.refresh_token.usecase.create;

import java.time.Instant;

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
    String token = hashService.hash(command.userId().toString());
    String tokenHash = hashService.hash(token);
    Instant expiresAt = Instant.now().plusSeconds(60 * 60 * 24 * 7);

    RefreshToken refreshToken = command.toModel(tokenHash, expiresAt);
    refreshTokenRepository.save(refreshToken);

    return token;
  }

}
