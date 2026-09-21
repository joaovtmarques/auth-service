package com.joaovtmarques.auth.infra.persistence.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.joaovtmarques.auth.domain.refresh_token.model.RefreshToken;
import com.joaovtmarques.auth.domain.refresh_token.repository.RefreshTokenRepository;
import com.joaovtmarques.auth.infra.persistence.entity.RefreshTokenEntity;
import com.joaovtmarques.auth.infra.persistence.repository.SpringDataRefreshTokenRepository;

@Component
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

  private final SpringDataRefreshTokenRepository springDataRefreshTokenRepository;

  public RefreshTokenRepositoryImpl(SpringDataRefreshTokenRepository springDataRefreshTokenRepository) {
    this.springDataRefreshTokenRepository = springDataRefreshTokenRepository;
  }

  @Override
  public RefreshToken save(RefreshToken refreshToken) {
    RefreshTokenEntity entity = toEntity(refreshToken);
    RefreshTokenEntity savedEntity = springDataRefreshTokenRepository.save(entity);
    return toDomain(savedEntity);
  }

  @Override
  public Optional<RefreshToken> findByTokenHash(String tokenHash) {
    return springDataRefreshTokenRepository.findByTokenHash(tokenHash)
        .map(this::toDomain);
  }

  @Override
  public void deleteByUserId(UUID userId) {
    springDataRefreshTokenRepository.deleteByUserId(userId);
  }

  private RefreshTokenEntity toEntity(RefreshToken refreshToken) {
    return new RefreshTokenEntity(
        refreshToken.getId(),
        refreshToken.getUserId(),
        refreshToken.getTokenHash(),
        refreshToken.getExpiresAt(),
        refreshToken.getRevokedAt(),
        refreshToken.getReplacedByTokenHash(),
        refreshToken.getIpAddress(),
        refreshToken.getUserAgent(),
        refreshToken.getCreatedAt());
  }

  private RefreshToken toDomain(RefreshTokenEntity entity) {
    return new RefreshToken(
        entity.getId(),
        entity.getUserId(),
        entity.getTokenHash(),
        entity.getExpiresAt(),
        entity.getRevokedAt(),
        entity.getReplacedByTokenHash(),
        entity.getIpAddress(),
        entity.getUserAgent(),
        entity.getCreatedAt());
  }
}
