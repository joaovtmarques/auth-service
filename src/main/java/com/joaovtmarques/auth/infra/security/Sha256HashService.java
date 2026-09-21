package com.joaovtmarques.auth.infra.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import org.springframework.stereotype.Component;

import com.joaovtmarques.auth.domain.common.service.HashService;

@Component
public class Sha256HashService implements HashService {

  @Override
  public String hash(String rawText) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] encodedHash = digest.digest(rawText.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(encodedHash);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Error hashing raw text with SHA-256", e);
    }
  }
}