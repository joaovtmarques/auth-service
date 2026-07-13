package com.joaovtmarques.auth.domain.user.usecase.authenticate;

public interface AuthenticateUserUseCase {

  String execute(AuthenticateUserCommand command);

}
