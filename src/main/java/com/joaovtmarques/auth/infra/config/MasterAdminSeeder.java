package com.joaovtmarques.auth.infra.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.joaovtmarques.auth.domain.role.model.Role;
import com.joaovtmarques.auth.domain.role.repository.RoleRepository;
import com.joaovtmarques.auth.domain.user.model.User;
import com.joaovtmarques.auth.domain.user.repository.UserRepository;
import com.joaovtmarques.auth.domain.user.service.PasswordEncryptor;

@Component
public class MasterAdminSeeder implements CommandLineRunner {

  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private final PasswordEncryptor passwordEncryptor;

  @Value("${api.security.master.email:master@auth.local}")
  private String masterEmail;

  @Value("${api.security.master.password:Master@123456}")
  private String masterPassword;

  public MasterAdminSeeder(
      RoleRepository roleRepository,
      UserRepository userRepository,
      PasswordEncryptor passwordEncryptor) {
    this.roleRepository = roleRepository;
    this.userRepository = userRepository;
    this.passwordEncryptor = passwordEncryptor;
  }

  @Override
  public void run(String... args) {
    Role masterRole = roleRepository.findByName("AUTH_MASTER")
        .orElseGet(() -> roleRepository.save(
            Role.create("AUTH_MASTER", "Master administrator for role and access management")));

    if (userRepository.findByEmail(masterEmail).isEmpty()) {
      User masterUser = new User();
      masterUser.setName("Master Admin");
      masterUser.setEmail(masterEmail);
      masterUser.setPasswordHash(passwordEncryptor.encrypt(masterPassword));
      masterUser.assignRole(masterRole);
      userRepository.save(masterUser);
    }
  }
}