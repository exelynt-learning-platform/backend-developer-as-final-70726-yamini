package com.example.demo.security;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    // Generate JWT token
    public String generateToken(String email) {

        Date currentDate = new Date();

        Date expirationDate =
                new Date(currentDate.getTime() + jwtExpiration);

        return Jwts.builder()
                .subject(email)
                .issuedAt(currentDate)
                .expiration(expirationDate)
                .signWith(getSigningKey())
                .compact();
    }

    // Extract email from JWT
    public String extractUsername(String token) {

        return extractAllClaims(token)
                .getSubject();
    }

    // Extract all claims
    private Claims extractAllClaims(String token) {

    	return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Check whether token is expired
    public boolean isTokenExpired(String token) {

        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }

    // Validate token
    public boolean isTokenValid(String token, String email) {

        String username = extractUsername(token);

        return username.equals(email)
                && !isTokenExpired(token);
    }

    // Create signing key
    private Key getSigningKey() {
        byte[] keyBytes;

        if (secretKey == null || secretKey.trim().isEmpty()) {
            // Generate a secure random 256-bit key for development/runtime when JWT_SECRET is not provided.
            logger.warn("JWT secret not set. Generating a temporary secret for this runtime. Set JWT_SECRET in production.");
            byte[] randomBytes = new byte[32]; // 256 bits
            new SecureRandom().nextBytes(randomBytes);
            keyBytes = randomBytes;
        } else {
            try {
                keyBytes = Decoders.BASE64.decode(secretKey);
            } catch (IllegalArgumentException ex) {
                // provide clearer message if not valid base64
                throw new IllegalStateException("JWT secret is not valid Base64-encoded key", ex);
            }
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }
}