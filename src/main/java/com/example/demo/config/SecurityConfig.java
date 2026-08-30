package com.example.demo.config;

import jakarta.servlet.DispatcherType;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.demo.security.JwtAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

            .csrf(csrf -> csrf.disable())

            // Add common security headers to mitigate clickjacking, MIME sniffing,
            // and enforce HSTS for HTTPS deployments.
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
                .contentTypeOptions(contentTypeOptions -> {})
                .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000))
            )

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .formLogin(form -> form.disable())

            .httpBasic(basic -> basic.disable())

            .authorizeHttpRequests(auth -> auth

                // Public MVC views and Static resources
                .requestMatchers(
                    "/",
                    "/login",
                    "/register",
                    "/home",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/WEB-INF/**"
                ).permitAll()

                // Admin
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // Authentication APIs
                .requestMatchers("/api/auth/**").permitAll()

                // Swagger
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**"
                ).permitAll()

                // Everything else requires authentication
                .anyRequest().authenticated()
            )

            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}