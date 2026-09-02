package com.joaovtmarques.auth.infra.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.joaovtmarques.auth.domain.user.exception.InvalidCredentialsException;
import com.joaovtmarques.auth.domain.user.exception.UserAlreadyExistsException;
import com.joaovtmarques.auth.domain.user.exception.UserNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ValidationError> handleValidationExceptions(MethodArgumentNotValidException ex) {
    var fieldErrors = ex.getBindingResult().getFieldErrors().stream()
        .map(f -> new ValidationError.FieldErrorDetail(f.getField(), f.getDefaultMessage()))
        .toList();

    var error = new ValidationError(
        "Bad Request",
        "Validation failed for one or more fields",
        HttpStatus.BAD_REQUEST.value(),
        System.currentTimeMillis(),
        fieldErrors);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<StandardError> handleUserAlreadyExists(UserAlreadyExistsException ex) {
    var error = new StandardError(
        "Conflict",
        ex.getMessage(),
        HttpStatus.CONFLICT.value(),
        System.currentTimeMillis());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<StandardError> handleInvalidCredentials(InvalidCredentialsException ex) {
    var error = new StandardError(
        "Unauthorized",
        ex.getMessage(),
        HttpStatus.UNAUTHORIZED.value(),
        System.currentTimeMillis());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<StandardError> handleUserNotFound(UserNotFoundException ex) {
    var error = new StandardError(
        "Not Found",
        ex.getMessage(),
        HttpStatus.NOT_FOUND.value(),
        System.currentTimeMillis());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<StandardError> handleGenericException(Exception ex) {
    var error = new StandardError(
        "Internal Server Error",
        "An unexpected error occurred",
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        System.currentTimeMillis());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }
}