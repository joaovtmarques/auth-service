package com.joaovtmarques.auth.domain.refresh_token.usecase.rotate;

import java.util.List;
import java.util.UUID;

import com.joaovtmarques.auth.domain.common.service.HashService;
import com.joaovtmarques.auth.domain.refresh_token.exception.InvalidRefreshTokenException;
import com.joaovtmarques.auth.domain.refresh_token.model.RefreshToken;
import com.joaovtmarques.auth.domain.refresh_token.repository.RefreshTokenRepository;
import com.joaovtmarques.auth.domain.refresh_token.usecase.create.CreateRefreshTokenCommand;
import com.joaovtmarques.auth.domain.refresh_token.usecase.create.CreateRefreshTokenUseCase;
import com.joaovtmarques.auth.domain.role.model.Role;
import com.joaovtmarques.auth.domain.user.exception.UserNotFoundException;
import com.joaovtmarques.auth.domain.user.model.User;
import com.joaovtmarques.auth.domain.user.repository.UserRepository;
import com.joaovtmarques.auth.domain.user.service.TokenService;

public class RotateRefreshTokenUseCaseImpl implements RotateRefreshTokenUseCase {

  private final RefreshTokenRepository refreshTokenRepository;
  private final UserRepository userRepository;
  private final HashService hashService;
  private final TokenService tokenService;
  private final CreateRefreshTokenUseCase createRefreshTokenUseCase;

  public RotateRefreshTokenUseCaseImpl(
      RefreshTokenRepository refreshTokenRepository,
      UserRepository userRepository,
      HashService hashService,
      TokenService tokenService,
      CreateRefreshTokenUseCase createRefreshTokenUseCase) {
    this.refreshTokenRepository = refreshTokenRepository;
    this.userRepository = userRepository;
    this.hashService = hashService;
    this.tokenService = tokenService;
    this.createRefreshTokenUseCase = createRefreshTokenUseCase;
  }

  @Override
  public RotateRefreshTokenResult execute(RotateRefreshTokenCommand command) {
    String incomingTokenHash = hashService.hash(command.refreshToken());

    RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(incomingTokenHash)
        .orElseThrow(() -> new InvalidRefreshTokenException("Invalid or not found refresh token."));

    UUID userId = refreshToken.getUserId();

    if (refreshToken.isRevoked()) {
      refreshTokenRepository.deleteByUserId(userId);
      throw new InvalidRefreshTokenException(
          "Attempt to reuse a token detected. All sessions have been terminated for security reasons.");
    }

    if (refreshToken.isExpired()) {
      throw new InvalidRefreshTokenException("Refresh token expired. Please log in again.");
    }

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

    CreateRefreshTokenCommand createCommand = new CreateRefreshTokenCommand(
        userId,
        command.ipAddress(),
        command.userAgent());
    String newPlainRefreshToken = createRefreshTokenUseCase.execute(createCommand);
    String newRefreshTokenHash = hashService.hash(newPlainRefreshToken);

    refreshToken.replaceWith(newRefreshTokenHash);
    refreshTokenRepository.save(refreshToken);

    List<String> roles = user.getRoles().stream()
        .map(Role::getName)
        .toList();

    String newAccessToken = tokenService.generateToken(userId.toString(), roles);

    return new RotateRefreshTokenResult(newAccessToken, newPlainRefreshToken);
  }
}