package com.gestor_empresarial.auth_server.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collections;

import com.gestor_empresarial.auth_server.enums.Role;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private final String secretKey = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"; // 256-bit
                                                                                                         // secret
    private final long jwtExpiration = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", jwtExpiration);
    }

    @Test
    void generateToken_ShouldReturnToken() {
        String token = jwtService.generateToken(1L, "test@example.com", Set.of(Role.ROLE_USER));
        assertNotNull(token);
    }

    @Test
    void extractEmail_ShouldReturnEmail() {
        String token = jwtService.generateToken(1L, "test@example.com", Set.of(Role.ROLE_USER));
        String email = jwtService.extractEmail(token);
        assertEquals("test@example.com", email);
    }

    @Test
    void extractUserId_ShouldReturnUserId() {
        String token = jwtService.generateToken(1L, "test@example.com", Set.of(Role.ROLE_USER));
        String userId = jwtService.extractUserId(token);
        assertEquals("1", userId);
    }

    @Test
    void validateToken_ShouldReturnTrue_WhenTokenIsValid() {
        String token = jwtService.generateToken(1L, "test@example.com", Set.of(Role.ROLE_USER));
        UserDetails userDetails = new User("test@example.com", "password", Collections.emptyList());

        assertTrue(jwtService.validateToken(token, userDetails));
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenUsernameDoesNotMatch() {
        String token = jwtService.generateToken(1L, "test@example.com", Set.of(Role.ROLE_USER));
        UserDetails userDetails = new User("other@example.com", "password", Collections.emptyList());

        assertFalse(jwtService.validateToken(token, userDetails));
    }

    @Test
    void isTokenValid_ShouldReturnTrue_WhenTokenIsValid() {
        String token = jwtService.generateToken(1L, "test@example.com", Set.of(Role.ROLE_USER));
        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void isTokenValid_ShouldReturnFalse_WhenTokenIsInvalid() {
        assertFalse(jwtService.isTokenValid("invalid.token.here"));
    }
}
