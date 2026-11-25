package com.joaovtmarques.auth.domain.user.service;

import java.util.List;

public interface TokenService {

  String generateToken(String userId, List<String> roles);

}
