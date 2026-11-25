package com.joaovtmarques.auth.infra.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.joaovtmarques.auth.domain.refresh_token.exception.InvalidRefreshTokenException;
import com.joaovtmarques.auth.domain.role.exception.LastMasterAdminRemovalException;
import com.joaovtmarques.auth.domain.role.exception.RoleAlreadyExistsException;
import com.joaovtmarques.auth.domain.role.exception.RoleNotFoundException;
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

  @ExceptionHandler(RoleAlreadyExistsException.class)
  public ResponseEntity<StandardError> handleRoleAlreadyExists(RoleAlreadyExistsException ex) {
    var error = new StandardError(
        "Conflict",
        ex.getMessage(),
        HttpStatus.CONFLICT.value(),
        System.currentTimeMillis());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }

  @ExceptionHandler(LastMasterAdminRemovalException.class)
  public ResponseEntity<StandardError> handleLastMasterAdminRemoval(LastMasterAdminRemovalException ex) {
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

  @ExceptionHandler(RoleNotFoundException.class)
  public ResponseEntity<StandardError> handleRoleNotFound(RoleNotFoundException ex) {
    var error = new StandardError(
        "Not Found",
        ex.getMessage(),
        HttpStatus.NOT_FOUND.value(),
        System.currentTimeMillis());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(InvalidRefreshTokenException.class)
  public ResponseEntity<ProblemDetail> handleInvalidRefreshToken(InvalidRefreshTokenException ex) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
    problemDetail.setTitle("Invalid Refresh Token");
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail);
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

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<StandardError> handleAccessDenied(AccessDeniedException ex) {
    var error = new StandardError(
        "Forbidden",
        "Access denied",
        HttpStatus.FORBIDDEN.value(),
        System.currentTimeMillis());
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
  }
}