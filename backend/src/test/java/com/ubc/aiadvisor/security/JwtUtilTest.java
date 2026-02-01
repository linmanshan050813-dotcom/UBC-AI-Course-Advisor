package com.ubc.aiadvisor.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JwtUtilTest tests JWT token parsing and user ID extraction.
 * Tests authentication and authorization logic critical for API security.
 */
class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String testSecret = "test-secret-key-for-jwt-signing-must-be-long-enough";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", testSecret);
    }

    @Test
    @DisplayName("getUserIdFromToken extracts user ID from valid Bearer token")
    void extractUserIdFromValidBearerToken() {
        // Arrange
        String userId = "test-user-123";
        String token = generateToken(userId, 3600000); // 1 hour expiry
        String bearerToken = "Bearer " + token;

        // Act
        String extractedUserId = jwtUtil.getUserIdFromToken(bearerToken);

        // Assert
        assertNotNull(extractedUserId);
        assertEquals(userId, extractedUserId);
    }

    @Test
    @DisplayName("getUserIdFromToken extracts user ID from token without Bearer prefix")
    void extractUserIdFromTokenWithoutBearer() {
        // Arrange
        String userId = "user-456";
        String token = generateToken(userId, 3600000);

        // Act
        String extractedUserId = jwtUtil.getUserIdFromToken(token);

        // Assert
        assertNotNull(extractedUserId);
        assertEquals(userId, extractedUserId);
    }

    @Test
    @DisplayName("getUserIdFromToken returns null for null token")
    void returnNullForNullToken() {
        // Act
        String result = jwtUtil.getUserIdFromToken(null);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("getUserIdFromToken returns null for empty token")
    void returnNullForEmptyToken() {
        // Act
        String result = jwtUtil.getUserIdFromToken("");

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("getUserIdFromToken returns null for malformed token")
    void returnNullForMalformedToken() {
        // Arrange
        String malformedToken = "Bearer not.a.valid.token";

        // Act
        String result = jwtUtil.getUserIdFromToken(malformedToken);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("getUserIdFromToken returns null for token with invalid signature")
    void returnNullForInvalidSignature() {
        // Arrange - Generate token with different secret
        String wrongSecret = "wrong-secret-key-different-from-test-secret";
        String userId = "user-789";
        String token = Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SignatureAlgorithm.HS256, wrongSecret.getBytes())
                .compact();
        String bearerToken = "Bearer " + token;

        // Act
        String result = jwtUtil.getUserIdFromToken(bearerToken);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("getUserIdFromToken returns null for expired token")
    void returnNullForExpiredToken() {
        // Arrange - Generate token that expired 1 hour ago
        String userId = "expired-user";
        String token = generateToken(userId, -3600000); // Expired 1 hour ago
        String bearerToken = "Bearer " + token;

        // Act
        String result = jwtUtil.getUserIdFromToken(bearerToken);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("getUserIdFromToken handles token with only 'Bearer ' prefix")
    void handleBearerPrefixOnly() {
        // Arrange
        String bearerToken = "Bearer ";

        // Act
        String result = jwtUtil.getUserIdFromToken(bearerToken);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("getUserIdFromToken handles whitespace token")
    void handleWhitespaceToken() {
        // Arrange
        String whitespaceToken = "Bearer    ";

        // Act
        String result = jwtUtil.getUserIdFromToken(whitespaceToken);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("getUserIdFromToken extracts correct user ID from token with special characters")
    void extractUserIdWithSpecialCharacters() {
        // Arrange
        String userId = "user@test.com";
        String token = generateToken(userId, 3600000);
        String bearerToken = "Bearer " + token;

        // Act
        String extractedUserId = jwtUtil.getUserIdFromToken(bearerToken);

        // Assert
        assertNotNull(extractedUserId);
        assertEquals(userId, extractedUserId);
    }

    @Test
    @DisplayName("getUserIdFromToken extracts UUID format user ID")
    void extractUuidFormatUserId() {
        // Arrange
        String userId = "550e8400-e29b-41d4-a716-446655440000";
        String token = generateToken(userId, 3600000);
        String bearerToken = "Bearer " + token;

        // Act
        String extractedUserId = jwtUtil.getUserIdFromToken(bearerToken);

        // Assert
        assertNotNull(extractedUserId);
        assertEquals(userId, extractedUserId);
    }

    @Test
    @DisplayName("getUserIdFromToken handles token with extra spaces after Bearer")
    void handleExtraSpacesAfterBearer() {
        // Arrange
        String userId = "user-with-spaces";
        String token = generateToken(userId, 3600000);
        String bearerToken = "Bearer  " + token; // Extra space

        // Act
        String extractedUserId = jwtUtil.getUserIdFromToken(bearerToken);

        // Assert
        // JWT parser is tolerant of extra spaces, so it actually extracts successfully
        assertNotNull(extractedUserId);
        assertEquals(userId, extractedUserId);
    }

    @Test
    @DisplayName("getUserIdFromToken handles case-sensitive Bearer prefix")
    void handleCaseSensitiveBearerPrefix() {
        // Arrange
        String userId = "case-test-user";
        String token = generateToken(userId, 3600000);
        String lowerCaseBearer = "bearer " + token;

        // Act
        String result = jwtUtil.getUserIdFromToken(lowerCaseBearer);

        // Assert
        assertNull(result); // Should fail - "bearer" != "Bearer"
    }

    @Test
    @DisplayName("getUserIdFromToken extracts user ID from recently issued token")
    void extractFromRecentlyIssuedToken() {
        // Arrange
        String userId = "recent-user";
        String token = Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000)) // Issued 1 second ago
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SignatureAlgorithm.HS256, testSecret.getBytes())
                .compact();
        String bearerToken = "Bearer " + token;

        // Act
        String extractedUserId = jwtUtil.getUserIdFromToken(bearerToken);

        // Assert
        assertNotNull(extractedUserId);
        assertEquals(userId, extractedUserId);
    }

    @Test
    @DisplayName("getUserIdFromToken handles long user ID")
    void handleLongUserId() {
        // Arrange
        String userId = "a".repeat(256); // Very long user ID
        String token = generateToken(userId, 3600000);
        String bearerToken = "Bearer " + token;

        // Act
        String extractedUserId = jwtUtil.getUserIdFromToken(bearerToken);

        // Assert
        assertNotNull(extractedUserId);
        assertEquals(userId, extractedUserId);
    }

    // Helper method to generate JWT tokens for testing
    private String generateToken(String userId, long expiryOffset) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiryOffset))
                .signWith(SignatureAlgorithm.HS256, testSecret.getBytes())
                .compact();
    }
}
