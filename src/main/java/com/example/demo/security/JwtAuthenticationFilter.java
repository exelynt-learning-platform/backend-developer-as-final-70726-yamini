package com.example.demo.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

        // =====================================================
        // 1. Get Authorization header
        // =====================================================

        String authHeader = request.getHeader("Authorization");

        // No JWT → continue request
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        // =====================================================
        // 2. Extract JWT token
        // =====================================================

        String token = authHeader.substring(7).trim();

        if (token.isEmpty()) {

            filterChain.doFilter(request, response);
            return;
        }

        try {

            // =================================================
            // 3. Extract username/email from JWT
            // =================================================

            String email = jwtService.extractUsername(token);

            if (email == null || email.isBlank()) {

                logger.warn("JWT does not contain username/email");

                filterChain.doFilter(request, response);
                return;
            }

            // =================================================
            // 4. Check whether user is already authenticated
            // =================================================

            if (SecurityContextHolder.getContext()
                    .getAuthentication() == null) {

                // =============================================
                // 5. Validate JWT
                // =============================================

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

                // =============================================
                // 6. Load user from database
                // =============================================

                UserDetails userDetails =
                        customUserDetailsService
                                .loadUserByUsername(email);

                // =============================================
                // 7. Create Authentication object
                // =============================================

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // =============================================
                // 8. Add request details
                // =============================================

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                // =============================================
                // 9. Store authentication in SecurityContext
                // =============================================

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);

                logger.debug(
                        "JWT authentication successful for user: {}",
                        email
                );
            }

        } catch (JwtException
                | IllegalStateException ex) {

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
                            "Invalid or expired JWT token"
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

        // =====================================================
        // 10. Continue request
        // =====================================================

        filterChain.doFilter(request, response);
    }
}