package com.joaovtmarques.auth.domain.refresh_token.repository;

import java.util.Optional;
import java.util.UUID;

import com.joaovtmarques.auth.domain.refresh_token.model.RefreshToken;

public interface RefreshTokenRepository {

  Optional<RefreshToken> findById(UUID id);

  void save(RefreshToken refreshToken);

  void delete(RefreshToken refreshToken);

}
