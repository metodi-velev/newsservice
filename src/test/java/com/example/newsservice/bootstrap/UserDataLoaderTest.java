package com.example.newsservice.bootstrap;

import com.example.newsservice.repository.RoleRepository;
import com.example.newsservice.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(BCryptPasswordEncoder.class)
class UserDataLoaderTest {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserDataLoader userDataLoader;

    @BeforeEach
    void setUp() {
        this.userDataLoader = new UserDataLoader(roleRepository, userRepository, passwordEncoder);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void run() throws Exception {
        userDataLoader.run("");

        assertThat(roleRepository.count()).isEqualTo(3);
        assertThat(userRepository.count()).isEqualTo(3);
    }
}