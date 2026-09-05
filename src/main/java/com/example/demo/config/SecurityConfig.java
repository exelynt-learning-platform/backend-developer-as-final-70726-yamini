package com.example.demo.config;

import jakarta.servlet.DispatcherType;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.example.demo.exception.AuthenticationException;
import com.example.demo.security.JwtAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final HandlerExceptionResolver resolver;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            @Qualifier("handlerExceptionResolver")
            HandlerExceptionResolver resolver) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.resolver = resolver;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .cors(Customizer.withDefaults())

            .csrf(csrf -> csrf.disable())

            .exceptionHandling(ex ->
                ex.authenticationEntryPoint((request, response, authException) ->
                    resolver.resolveException(
                        request,
                        response,
                        null,
                        new AuthenticationException(authException.getMessage())
                    )
                )
            )

            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .formLogin(form -> form.disable())

            .httpBasic(basic -> basic.disable())

            .authorizeHttpRequests(auth -> auth

                // Allow JSP internal forwards and error pages
                .dispatcherTypeMatchers(
                    DispatcherType.FORWARD,
                    DispatcherType.ERROR
                ).permitAll()

                // Public pages
                .requestMatchers(
                    "/",
                    "/login",
                    "/register",
                    "/home",
                    "/favicon.ico"
                ).permitAll()

                // Public static resources
                .requestMatchers(
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/fonts/**",
                    "/webjars/**"
                ).permitAll()

                // Authentication APIs
                .requestMatchers("/api/auth/**").permitAll()

                // Swagger
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**"
                ).permitAll()

                // Admin JSP page
                .requestMatchers("/admin").hasRole("ADMIN")

                // Admin APIs require ADMIN role
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

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