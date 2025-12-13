package com.gestor_empresarial.auth_server.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.gestor_empresarial.auth_server.dtos.AuthResponseDto;
import com.gestor_empresarial.auth_server.dtos.LoginRequestDto;
import com.gestor_empresarial.auth_server.dtos.RegisterRequestDto;
import com.gestor_empresarial.auth_server.enums.Role;
import com.gestor_empresarial.auth_server.exceptions.BadCredentialsException;
import com.gestor_empresarial.auth_server.exceptions.UserAlreadyExists;
import com.gestor_empresarial.auth_server.exceptions.UserNotFoundException;
import com.gestor_empresarial.auth_server.models.User;
import com.gestor_empresarial.auth_server.repositories.IUserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private IUserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private org.springframework.kafka.core.KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private AuthService authService;

    private RegisterRequestDto registerRequest;
    private LoginRequestDto loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequestDto("John", "Doe", "john@example.com", "password");
        loginRequest = new LoginRequestDto("john@example.com", "password");

        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@example.com");
        user.setPassword("encodedPassword");
        user.setRoles(new java.util.HashSet<>(Set.of(Role.ROLE_USER)));
    }

    @Test
    void register_ShouldReturnAuthResponse_WhenEmailIsUnique() {
        when(repository.existsByEmail(registerRequest.email())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.password())).thenReturn("encodedPassword");
        when(repository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(), any(), any())).thenReturn("jwt-token");

        AuthResponseDto response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.token());
        assertEquals("john@example.com", response.email());
        verify(repository).save(any(User.class));
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        when(repository.existsByEmail(registerRequest.email())).thenReturn(true);

        assertThrows(UserAlreadyExists.class, () -> authService.register(registerRequest));
        verify(repository, never()).save(any(User.class));
    }

    @Test
    void login_ShouldReturnAuthResponse_WhenCredentialsAreValid() {
        when(repository.findByEmail(loginRequest.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.password(), user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(any(), any(), any())).thenReturn("jwt-token");

        AuthResponseDto response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.token());
    }

    @Test
    void login_ShouldThrowException_WhenUserNotFound() {
        when(repository.findByEmail(loginRequest.email())).thenReturn(Optional.empty());

        UserNotFoundException thrown = assertThrows(
                UserNotFoundException.class,
                () -> authService.login(loginRequest),
                "Debe lanzar RunTimeException cuando no existe el usuario");

        assertEquals("Usuario no encontrado", thrown.getMessage());
    }

    @Test
    void login_ShouldThrowException_WhenPasswordIsInvalid() {
        when(repository.findByEmail(loginRequest.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.password(), user.getPassword())).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
    }

    @Test
    void findAll_ShouldReturnList() {
        when(repository.findAll()).thenReturn(java.util.List.of(user));
        assertFalse(authService.findAll().isEmpty());
    }

    @Test
    void getProfile_ShouldReturnUser_WhenFound() {
        when(repository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        assertNotNull(authService.getProfile("john@example.com"));
    }

    @Test
    void getProfileByEmail_ShouldReturnDto_WhenFound() {
        when(repository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        assertNotNull(authService.getProfileByEmail("john@example.com"));
    }

    @Test
    void deleteUser_ShouldDelete_WhenFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        authService.deleteUser(1L);
        verify(repository).delete(user);
    }

    @Test
    void updatedUserRole_ShouldUpdate_WhenUserFoundAndRoleValid() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        com.gestor_empresarial.auth_server.dtos.UserRoleUpdateDto updateDto = new com.gestor_empresarial.auth_server.dtos.UserRoleUpdateDto(
                "ROLE_ADMIN");

        authService.updatedUserRole(1L, updateDto);

        verify(repository).save(user);
        assertTrue(user.getRoles().contains(Role.ROLE_ADMIN));
    }

    @Test
    void getUserNameAndEmail_ShouldReturnNotificationDto() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        com.gestor_empresarial.auth_server.dtos.UserNotificationDto dto = authService.getUserNameAndEmail(1L);
        assertEquals("john@example.com", dto.userEmail());
    }
}
