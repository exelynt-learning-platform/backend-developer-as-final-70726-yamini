package com.example.demo.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.example.demo.exception.AuthenticationException;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger =
            LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final HandlerExceptionResolver resolver;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService customUserDetailsService,
            @Qualifier("handlerExceptionResolver")
            HandlerExceptionResolver resolver) {

        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // Get Authorization header
        String authHeader = request.getHeader("Authorization");

        // No JWT token -> continue normally
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract token
        String token = authHeader.substring(7).trim();

        // Empty token -> continue
        if (token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract email from JWT
            String email = jwtService.extractUsername(token);

            // Only authenticate if user is not already authenticated
            if (email != null
                    && SecurityContextHolder.getContext()
                            .getAuthentication() == null) {

                // Validate token BEFORE database lookup
                if (!jwtService.isTokenValid(token, email)) {

                    logger.warn(
                            "Invalid or expired JWT token for email: {}",
                            email
                    );

                    SecurityContextHolder.clearContext();

                    resolver.resolveException(
                            request,
                            response,
                            null,
                            new AuthenticationException(
                                    "Invalid or expired JWT token"
                            )
                    );

                    return;
                }

                // Load user from database
                UserDetails userDetails =
                        customUserDetailsService.loadUserByUsername(email);

                // Create authentication
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // Add request details
                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                // Store authentication
                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);

                logger.debug(
                        "JWT authentication successful for user: {}",
                        email
                );
            }

        } catch (JwtException
                | IllegalStateException
                | UsernameNotFoundException ex) {

            logger.warn(
                    "JWT authentication failed: {}",
                    ex.getMessage()
            );

            SecurityContextHolder.clearContext();

            resolver.resolveException(
                    request,
                    response,
                    null,
                    new AuthenticationException(
                            ex.getMessage()
                    )
            );

            return;

        } catch (Exception ex) {

            logger.error(
                    "Unexpected error in JWT authentication filter",
                    ex
            );

            SecurityContextHolder.clearContext();

            resolver.resolveException(
                    request,
                    response,
                    null,
                    new AuthenticationException(
                            "Authentication failed"
                    )
            );

            return;
        }

        // Continue request
        filterChain.doFilter(request, response);
    }
}
