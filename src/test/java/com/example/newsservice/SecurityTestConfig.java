package com.example.newsservice;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@TestConfiguration
public class SecurityTestConfig {

    @Bean
    public UserDetailsService userDetailsServiceTest() {
        UserDetails adminUser = new User("martin", "martin", Stream.of("ROLE_ADMIN")
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet()));

        UserDetails publisherUser = new User("lisa", "lisa", Stream.of("ROLE_PUBLISHER")
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet()));

        UserDetails readerUser = new User("john", "john", Stream.of("ROLE_READER")
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet()));

        return new InMemoryUserDetailsManager(Arrays.asList(
                adminUser, publisherUser, readerUser
        ));
    }
}