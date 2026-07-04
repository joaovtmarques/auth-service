package com.joaovtmarques.auth.domain.user.model;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "users")
public class User {

  @EqualsAndHashCode.Include
  @Id
  private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true, columnDefinition = "citext")
  private String email;

  @Column(nullable = false, unique = true)
  private String phone;

  @JsonIgnore
  @Column(name = "password_hash")
  private String passwordHash;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  public void setName(String name) {
    this.name = name;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

}
