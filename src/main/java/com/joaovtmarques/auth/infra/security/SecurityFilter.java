package com.joaovtmarques.auth.infra.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.joaovtmarques.auth.domain.user.repository.UserRepository;
import com.joaovtmarques.auth.infra.security.token.JwtTokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;

@Component
public class SecurityFilter extends OncePerRequestFilter {

  private final JwtTokenService tokenService;
  private final UserRepository userRepository;

  public SecurityFilter(JwtTokenService tokenService, UserRepository userRepository) {
    this.tokenService = tokenService;
    this.userRepository = userRepository;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    var token = this.recoverToken(request);

    if (token != null) {
      var email = tokenService.validateToken(token);

      if (!email.isEmpty()) {
        userRepository.findByEmail(email).ifPresent(user -> {
          var authentication = new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
          SecurityContextHolder.getContext().setAuthentication(authentication);
        });
      }
    }

    filterChain.doFilter(request, response);

  }

  private String recoverToken(HttpServletRequest request) {
    var authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return null;
    }
    return authHeader.substring(7);
  }
}