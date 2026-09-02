package com.joaovtmarques.auth.infra.web.exception;

import java.util.List;

public record ValidationError(
    String error,
    String message,
    int status,
    long timestamp,
    List<FieldErrorDetail> errors) {
  public record FieldErrorDetail(String field, String message) {
  }
}