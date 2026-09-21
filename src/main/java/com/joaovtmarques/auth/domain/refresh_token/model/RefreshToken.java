package com.joaovtmarques.auth.domain.refresh_token.model;

import java.time.Instant;
import java.util.UUID;

public class RefreshToken {

  private UUID id;
  private UUID userId;
  private String tokenHash;
  private Instant expiresAt;
  private Instant revokedAt;
  private String replacedByTokenHash;
  private String ipAddress;
  private String userAgent;
  private Instant createdAt;

  public RefreshToken() {
  }

  public RefreshToken(UUID id, UUID userId, String tokenHash, Instant expiresAt, Instant revokedAt,
      String replacedByTokenHash, String ipAddress, String userAgent, Instant createdAt) {
    this.id = id;
    this.userId = userId;
    this.tokenHash = tokenHash;
    this.expiresAt = expiresAt;
    this.revokedAt = revokedAt;
    this.replacedByTokenHash = replacedByTokenHash;
    this.ipAddress = ipAddress;
    this.userAgent = userAgent;
    this.createdAt = createdAt;
  }

  public UUID getId() {
    return id;
  }

  public UUID getUserId() {
    return userId;
  }

  public String getTokenHash() {
    return tokenHash;
  }

  public Instant getExpiresAt() {
    return expiresAt;
  }

  public Instant getRevokedAt() {
    return revokedAt;
  }

  public String getReplacedByTokenHash() {
    return replacedByTokenHash;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public String getUserAgent() {
    return userAgent;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public void setTokenHash(String tokenHash) {
    this.tokenHash = tokenHash;
  }

  public void setExpiresAt(Instant expiresAt) {
    this.expiresAt = expiresAt;
  }

  public void setRevokedAt(Instant revokedAt) {
    this.revokedAt = revokedAt;
  }

  public void setReplacedByTokenHash(String replacedByTokenHash) {
    this.replacedByTokenHash = replacedByTokenHash;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public boolean isExpired() {
    return expiresAt != null && Instant.now().isAfter(expiresAt);
  }

  public boolean isRevoked() {
    return revokedAt != null;
  }

  public boolean isActive() {
    return !isRevoked() && !isExpired();
  }

  public void revoke() {
    this.revokedAt = Instant.now();
  }

  public void replaceWith(String newTokenHash) {
    this.revoke();
    this.replacedByTokenHash = newTokenHash;
  }
}