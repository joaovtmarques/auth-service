package com.joaovtmarques.auth.infra.persistence.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RoleEntity {

  @EqualsAndHashCode.Include
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", updatable = false, unique = true, nullable = false)
  private UUID id;

  @Column(nullable = false, unique = true, length = 100)
  private String name;

  @Column(length = 255)
  private String description;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @PrePersist
  protected void onCreate() {
    if (this.createdAt == null) {
      this.createdAt = Instant.now();
    }
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

}