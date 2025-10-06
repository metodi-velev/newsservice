package com.example.newsservice;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Primary
@TestConfiguration
public class SecurityTestConfig {

    @Bean
    @Primary
    public UserDetailsService userDetailsServiceTest() {
        UserDetails adminUser = User.withUsername("martin")
                .password("{noop}martin")
                .roles("ADMIN")
                .build();

        UserDetails publisherUser = User.withUsername("lisa")
                .password("{noop}lisa")
                .roles("PUBLISHER")
                .build();

        UserDetails readerUser = User.withUsername("john")
                .password("{noop}john")
                .roles("READER")
                .build();

        return new InMemoryUserDetailsManager(adminUser, publisherUser, readerUser);
    }

    @Bean
    @Primary
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(authorize -> {
                    authorize
                            .requestMatchers("/h2-console/**").permitAll() //do not use in production!
                            .requestMatchers("/", "/webjars/**", "/login", "/resources/**").permitAll()
                            .requestMatchers(HttpMethod.GET, "/news/**")
                            .hasAnyRole("ADMIN", "READER")
                            .requestMatchers(HttpMethod.GET, "/public/**")
                            .hasAnyRole("ADMIN", "PUBLISHER", "READER")
                            .requestMatchers(HttpMethod.DELETE, "/news/**", "/news")
                            .hasAnyRole("ADMIN", "PUBLISHER")
                            .requestMatchers(HttpMethod.PUT, "/news", "/news/**")
                            .hasAnyRole("ADMIN", "PUBLISHER")
                            .requestMatchers(HttpMethod.POST, "/news")
                            .hasAnyRole("PUBLISHER");
                })
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable);

        //h2 console config
        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

        return http.build();
    }
}