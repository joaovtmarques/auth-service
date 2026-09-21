package com.joaovtmarques.auth.domain.refresh_token.repository;

import java.util.Optional;
import java.util.UUID;

import com.joaovtmarques.auth.domain.refresh_token.model.RefreshToken;

public interface RefreshTokenRepository {

  Optional<RefreshToken> findByTokenHash(String tokenHash);

  RefreshToken save(RefreshToken refreshToken);

  void deleteByUserId(UUID userId);

}
