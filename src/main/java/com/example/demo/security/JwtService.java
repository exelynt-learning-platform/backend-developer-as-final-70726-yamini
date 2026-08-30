package com.example.demo.security;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${app.jwt.secret:}")
    private String secretKey;

    @Value("${app.jwt.expiration:86400000}")
    private long jwtExpiration;

    private volatile Key signingKey;

    // Generate JWT token
    public String generateToken(String email) {
        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime() + jwtExpiration);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(currentDate)
                .setExpiration(expirationDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extract email from JWT
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
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
        return username.equals(email) && !isTokenExpired(token);
    }

    // Create signing key
    private Key getSigningKey() {
        if (signingKey != null) {
            return signingKey;
        }

        synchronized (this) {
            if (signingKey != null) {
                return signingKey;
            }

            if (secretKey == null || secretKey.trim().isEmpty()) {
                throw new IllegalStateException("JWT secret is not configured. Please set 'app.jwt.secret' in your configuration or JWT_SECRET in your environment.");
            }

            byte[] keyBytes;
            try {
                keyBytes = Decoders.BASE64.decode(secretKey);
            } catch (Exception ex) {
                keyBytes = secretKey.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            }

            if (keyBytes.length < 32) {
                throw new IllegalStateException("JWT secret must be at least 256 bits (32 bytes). Provide a Base64-encoded 256-bit key or a raw secret >= 32 bytes.");
            }

            signingKey = Keys.hmacShaKeyFor(keyBytes);
            return signingKey;
        }
    }
}