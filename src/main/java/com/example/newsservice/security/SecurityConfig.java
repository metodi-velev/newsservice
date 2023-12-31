package com.example.newsservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authorizeRequests(authorize -> {
                    authorize
                            .requestMatchers("/h2-console/**").permitAll() //do not use in production!
                            .requestMatchers("/", "/webjars/**", "/login", "/resources/**").permitAll()
                            .requestMatchers(HttpMethod.GET, "/news/**")
                            .hasAnyRole("ADMIN", "PUBLISHER", "READER")
                            .requestMatchers(HttpMethod.GET, "/public/**")
                            .hasAnyRole("ADMIN", "PUBLISHER", "READER")
                            .requestMatchers(HttpMethod.DELETE, "/news")
                            .hasAnyRole("ADMIN")
                            .requestMatchers(HttpMethod.PUT, "/news")
                            .hasAnyRole("ADMIN");
                })
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin().and()
                .httpBasic()
                .and().csrf().disable();

        //h2 console config
        http.headers().frameOptions().sameOrigin();

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return SfgPasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
