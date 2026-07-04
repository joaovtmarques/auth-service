package com.joaovtmarques.auth.domain.user.usecase.create;

import com.joaovtmarques.auth.domain.user.model.User;

public interface CreateUserUseCase {

  User execute(CreateUserCommand command);

}
