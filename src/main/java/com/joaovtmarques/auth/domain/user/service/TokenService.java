package com.joaovtmarques.auth.domain.user.service;

public interface TokenService {

  String generateToken(String userId);

}
