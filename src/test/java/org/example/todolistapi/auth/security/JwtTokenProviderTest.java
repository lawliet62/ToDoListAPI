package org.example.todolistapi.auth.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    JwtProperties props = new JwtProperties("sufficiently-long-secret-key-123456", 3600000L);
    JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(props);

    @Test
    void generateToken_returnsParsableTokenContainingUserId() {
        Long userId = 1L;

        String token = jwtTokenProvider.generateToken(userId);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals(userId, jwtTokenProvider.getUserIdFromToken(token));
    }

    @Test
    void getUserIdFromToken_throwsException_WhenSignatureIsInvalid() {
        JwtProperties otherProps = new JwtProperties(
                "another-very-long-secret-key-1234567890",
                3600000L
        );
        JwtTokenProvider otherProvider = new JwtTokenProvider(otherProps);

        String token = otherProvider.generateToken(1L);

        assertThrows(Exception.class, () -> jwtTokenProvider.getUserIdFromToken(token));
    }

    @Test
    void getUserIdFromToken_throwsException_whenTokenFormatIsInvalid() {
        assertThrows(Exception.class, () -> jwtTokenProvider.getUserIdFromToken("not-a-jwt"));
    }

}