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

@Slf4j
@RequiredArgsConstructor
@Order(1)
@Component
public class UserDataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        loadSecurityData();
    }

    private void loadSecurityData() {
        Role admin = roleRepository.save(Role.builder().roleName("ROLE_ADMIN").build());
        Role reader = roleRepository.save(Role.builder().roleName("ROLE_READER").build());
        Role publisher = roleRepository.save(Role.builder().roleName("ROLE_PUBLISHER").build());

        userRepository.save(User.builder()
                .username("martin")
                .password(passwordEncoder.encode("martin"))
                .role(admin)
                .build());

        userRepository.save(User.builder()
                .username("john")
                .password(passwordEncoder.encode("john"))
                .role(reader)
                .role(publisher)
                .build());

        userRepository.save(User.builder()
                .username("lisa")
                .password(passwordEncoder.encode("lisa"))
                .role(publisher)
                .build());

        log.debug("Users Loaded: " + userRepository.count());
    }
}
