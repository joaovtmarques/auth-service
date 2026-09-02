package com.joaovtmarques.auth.infra.web.exception;

public record StandardError(
    String error,
    String message,
    int status,
    long timestamp) {
}