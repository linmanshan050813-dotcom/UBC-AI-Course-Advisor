package com.ubc.aiadvisor.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * JWT Utility
 * Provides helper methods for parsing and validating JSON Web Tokens.
 * Extracts user ID and role information from Supabase-generated tokens.
 */
@Component
public class JwtUtil {

    @Value("${supabase.jwt.secret}")
    private String jwtSecret;

    /**
     * Extract user ID from JWT token.
     * 
     * @param token Bearer token string
     * @return user ID (sub claim) or null if invalid
     */
    public String getUserIdFromToken(String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            // Supabase JWT secret is often provided as a plain string, but JJWT expects bytes.
            // If the secret is Base64 encoded (common), decoding might be needed. 
            // However, typically for "HS256", it's just bytes.
            // Note: In production with Supabase, you should verify the signature.
            // For now, we'll try to parse it. If the secret is key-based, we use setSigningKey.
            
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtSecret.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Supabase stores user ID in "sub" claim
            return claims.getSubject();
        } catch (Exception e) {
            System.err.println("Error parsing JWT: " + e.getMessage());
            return null;
        }
    }

    /**
     * Extract user role from JWT token.
     * 
     * @param token Bearer token string
     * @return user role string or null if not present/invalid
     */
    public String getUserRoleFromToken(String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtSecret.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            Object roleClaim = claims.get("role");
            return roleClaim != null ? roleClaim.toString() : null;
        } catch (Exception e) {
            return null; // Silent failure defaults to null -> treated as unrestricted for backwards compatibility
        }
    }
}
