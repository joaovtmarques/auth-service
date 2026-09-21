package com.joaovtmarques.auth.infra.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class Sha256HashServiceTest {

  private final Sha256HashService hashService = new Sha256HashService();

  @Test
  @DisplayName("Should generate correct SHA-256 hash in hex format")
  void shouldGenerateSha256Hash() {
    String input = "test_token_string";
    String expectedHash = "674d787392e2de76d2ec07ed5bed79403abbe92f6ed53d5dc0bf25818b2c8d05";

    String result = hashService.hash(input);

    assertEquals(expectedHash, result);
  }

  @Test
  @DisplayName("Should produce different hashes for different inputs")
  void shouldProduceDifferentHashesForDifferentInputs() {
    String hash1 = hashService.hash("token_a");
    String hash2 = hashService.hash("token_b");

    assertNotEquals(hash1, hash2);
  }
}