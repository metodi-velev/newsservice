package com.example.newsservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private static final String ADMIN = "ADMIN";
    private static final String PUBLISHER = "PUBLISHER";
    private static final String READER = "READER";

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .securityMatcher(request -> !request.getRequestURI().startsWith("/h2-console"))
                .authorizeHttpRequests(authorize ->
                    authorize
                            .requestMatchers("/h2-console/**", "/h2-console").permitAll() //do not use in production!
                            .requestMatchers("/", "/webjars/**", "/login", "/resources/**").permitAll()
                            .requestMatchers(HttpMethod.GET, "/news/**")
                            .hasAnyRole(ADMIN, PUBLISHER, READER)
                            .requestMatchers(HttpMethod.GET, "/public/**")
                            .hasAnyRole(ADMIN, PUBLISHER, READER)
                            .requestMatchers(HttpMethod.DELETE, "/news")
                            .hasAnyRole(ADMIN)
                            .requestMatchers(HttpMethod.PUT, "/news")
                            .hasAnyRole(ADMIN)
                            .anyRequest().authenticated()
                )
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable);

        //h2 console config
        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return SfgPasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
