package com.retailcorp.retailshopping.unit.service;

import com.retailcorp.retailshopping.service.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(tokenProvider, "jwtSecret", "thisIsATestSecretKeyThatIsLongEnough123456");
        ReflectionTestUtils.setField(tokenProvider, "jwtExpiration", 86400000L);
    }

    @Test
    void generateToken_returnsValidToken() {
        String token = tokenProvider.generateToken("test@example.com");

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
    }

    @Test
    void getEmailFromToken_returnsCorrectEmail() {
        String token = tokenProvider.generateToken("test@example.com");

        String email = tokenProvider.getEmailFromToken(token);

        assertThat(email).isEqualTo("test@example.com");
    }

    @Test
    void validateToken_validToken_returnsTrue() {
        String token = tokenProvider.generateToken("test@example.com");

        boolean isValid = tokenProvider.validateToken(token);

        assertThat(isValid).isTrue();
    }

    @Test
    void validateToken_invalidToken_returnsFalse() {
        boolean isValid = tokenProvider.validateToken("invalid.token.here");

        assertThat(isValid).isFalse();
    }

    @Test
    void validateToken_malformedToken_returnsFalse() {
        boolean isValid = tokenProvider.validateToken("not-a-jwt-at-all");

        assertThat(isValid).isFalse();
    }

    @Test
    void validateToken_nullToken_returnsFalse() {
        boolean isValid = tokenProvider.validateToken(null);

        assertThat(isValid).isFalse();
    }

    @Test
    void validateToken_emptyToken_returnsFalse() {
        boolean isValid = tokenProvider.validateToken("");

        assertThat(isValid).isFalse();
    }
}