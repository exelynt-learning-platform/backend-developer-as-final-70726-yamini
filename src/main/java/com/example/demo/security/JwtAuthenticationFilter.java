package com.example.demo.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;



import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService customUserDetailsService) {

        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Get Authorization header
        String authHeader = request.getHeader("Authorization");

        // 2. Check whether Bearer token is present
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Remove "Bearer " from the header
        String token = authHeader.substring(7);

        try {
            // 4. Extract email from JWT
            String email = jwtService.extractUsername(token);

            // 5. Check whether user is already authenticated
            if (email != null
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 6. Load user from database
            UserDetails userDetails =
                customUserDetailsService.loadUserByUsername(email);

            // 7. Validate JWT
            if (jwtService.isTokenValid(token, email)) {

                // 8. Create authentication object
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );

                // 9. Add request details
                authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                        .buildDetails(request)
                );

                // 10. Store authentication in SecurityContext
                SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);
            }
            }

        } catch (io.jsonwebtoken.JwtException | IllegalStateException | UsernameNotFoundException ex) {
            // Known auth failures: token invalid/expired, misconfigured JWT secret, or missing user
            logger.warn("Authentication failed: {}", ex.getMessage());
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            String body = String.format("{\"error\":\"Unauthorized\",\"message\":\"%s\"}", ex.getMessage().replaceAll("\"","'"));
            response.getWriter().write(body);
            return;

        } catch (Exception e) {
            // Unexpected errors: clear context and continue so other filters/handlers can respond
            logger.error("Unexpected error in JWT filter", e);
            SecurityContextHolder.clearContext();
        }

        // 11. Continue request
        filterChain.doFilter(request, response);
    }
}