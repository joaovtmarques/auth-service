package com.joaovtmarques.auth.domain.refresh_token.usecase.create;

public interface CreateRefreshTokenUseCase {

  String execute(CreateRefreshTokenCommand command);

}
