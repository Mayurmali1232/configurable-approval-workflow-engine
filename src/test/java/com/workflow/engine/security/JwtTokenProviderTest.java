package com.workflow.engine.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;
    private final String secret = "myUltraSecretKeyForJwtSigningMustBeAtLeast32BytesLong!!";
    private final long expirationMs = 60000; // 1 minute

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider(secret, expirationMs);
    }

    @Test
    void testGenerateAndValidateToken_Success() {
        String token = tokenProvider.generateToken("mayur", "REQUESTER");
        
        assertNotNull(token);
        assertTrue(tokenProvider.validateToken(token));
        assertEquals("mayur", tokenProvider.getUsernameFromToken(token));
        assertEquals("ROLE_REQUESTER", tokenProvider.getRoleFromToken(token));
    }

    @Test
    void testValidateToken_InvalidOrMalformed_ReturnsFalse() {
        String invalidToken = "completely.invalid.tokenstring";
        assertFalse(tokenProvider.validateToken(invalidToken));
    }

    @Test
    void testValidateToken_EmptyOrNull_ReturnsFalse() {
        assertFalse(tokenProvider.validateToken(""));
        assertFalse(tokenProvider.validateToken(null));
    }
}