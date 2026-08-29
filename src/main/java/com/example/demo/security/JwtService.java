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

    @Value("${app.jwt.secret:}")
    private String secretKey;

    @Value("${app.jwt.expiration:86400000}")
    private long jwtExpiration;

    // cached signing key for this runtime
    private volatile java.security.Key signingKey;

    @Value("${app.jwt.allow-runtime-fallback:false}")
    private boolean allowRuntimeFallback;

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
        if (signingKey != null) return signingKey;

        synchronized (this) {
            if (signingKey != null) return signingKey;

            byte[] keyBytes;

            if (secretKey == null || secretKey.trim().isEmpty()) {
                if (!allowRuntimeFallback) {
                    throw new IllegalStateException("JWT secret not configured. Set app.jwt.secret or JWT_SECRET in environment for production.");
                }
                // Generate a secure random 256-bit key for development/runtime when explicitly allowed.
                logger.warn("JWT secret not set. Generating a temporary secret for this runtime because app.jwt.allow-runtime-fallback=true. Set JWT_SECRET in production.");
                byte[] randomBytes = new byte[32]; // 256 bits
                new SecureRandom().nextBytes(randomBytes);
                keyBytes = randomBytes;
            } else {
                // Prefer Base64-encoded key but accept a raw secret string as fallback
                try {
                    keyBytes = Decoders.BASE64.decode(secretKey);
                } catch (Exception ex) {
                    // Decoders.BASE64 throws DecodingException for invalid base64; fall back to raw bytes
                    keyBytes = secretKey.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                }

                if (keyBytes.length < 32) {
                    throw new IllegalStateException("JWT secret must be at least 256 bits (32 bytes). Provide a Base64-encoded 256-bit key or a raw secret >=32 bytes.");
                }
            }

            signingKey = Keys.hmacShaKeyFor(keyBytes);
            return signingKey;
        }
    }
}