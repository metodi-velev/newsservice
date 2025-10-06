package com.example.newsservice.bootstrap;

import com.example.newsservice.repository.RoleRepository;
import com.example.newsservice.repository.UserRepository;
import com.example.newsservice.security.Role;
import com.example.newsservice.security.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Order(1)
@Component
public class UserDataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        loadSecurityData();
    }

    private void loadSecurityData() {
        Role admin = createRoleIfNotFound("ROLE_ADMIN");
        Role reader = createRoleIfNotFound("ROLE_READER");
        Role publisher = createRoleIfNotFound("ROLE_PUBLISHER");

        createUserIfNotFound("martin", "martin", admin);
        createUserIfNotFound("john", "john", reader, publisher);
        createUserIfNotFound("lisa", "lisa", publisher);

        log.info("Users Loaded: " + userRepository.count());
    }

    private Role createRoleIfNotFound(String roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseGet(() -> roleRepository.save(Role.builder().roleName(roleName).build()));
    }

    private void createUserIfNotFound(String username, String password, Role... roles) {
        userRepository.findByUsername(username).ifPresentOrElse(
                u -> {}
                ,
                () -> {

                    User user =
                            User.builder()
                                    .username(username)
                                    .password(passwordEncoder.encode(password))
                                    .roles(Set.of(roles))
                                    .enabled(true)
                                    .accountNonExpired(true)
                                    .accountNonLocked(true)
                                    .credentialsNonExpired(true)
                                    .build();

                    userRepository.save(user);
                });
    }
}
