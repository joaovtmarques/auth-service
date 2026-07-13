package com.joaovtmarques.auth.domain.user.usecase.authenticate;

public record AuthenticateUserCommand(
    String email,
    String password) {

}
