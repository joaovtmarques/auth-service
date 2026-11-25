package com.joaovtmarques.auth.domain.role.model;

import java.time.Instant;
import java.util.UUID;

public class Role {

  private UUID id;
  private String name;
  private String description;
  private Instant createdAt;

  public Role() {
  }

  public Role(UUID id, String name, String description, Instant createdAt) {
    this.id = id;
    this.name = normalizeName(name);
    this.description = description;
    this.createdAt = createdAt;
  }

  public static Role create(String name, String description) {
    return new Role(
        UUID.randomUUID(),
        normalizeName(name),
        description,
        Instant.now());
  }

  private static String normalizeName(String name) {
    return name != null ? name.trim().toUpperCase() : null;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = normalizeName(name);
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}