package com.gestor_empresarial.auth_server.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestor_empresarial.auth_server.dtos.AuthResponseDto;
import com.gestor_empresarial.auth_server.dtos.LoginRequestDto;
import com.gestor_empresarial.auth_server.dtos.RegisterRequestDto;
import com.gestor_empresarial.auth_server.dtos.UserDto;
import com.gestor_empresarial.auth_server.enums.Role;
import com.gestor_empresarial.auth_server.models.User;
import com.gestor_empresarial.auth_server.services.AuthService;
import com.gestor_empresarial.auth_server.services.JwtService;
import com.gestor_empresarial.auth_server.services.JpaUserDetailsService;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for simplicity
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService; // Likely needed for security config if filters were on

    @MockitoBean
    private JpaUserDetailsService jpaUserDetailsService; // Likely needed for security config

    @Autowired
    private ObjectMapper objectMapper;

    private RegisterRequestDto registerRequest;
    private LoginRequestDto loginRequest;
    private AuthResponseDto authResponse;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequestDto("John", "Doe", "john@example.com", "password");
        loginRequest = new LoginRequestDto("john@example.com", "password");
        authResponse = new AuthResponseDto("token", "john@example.com", Set.of(Role.ROLE_USER));

        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@example.com");
        user.setRoles(Set.of(Role.ROLE_USER));

        userDto = new UserDto(1L, "John", "Doe", "john@example.com", Set.of("ROLE_USER"));
    }

    @Test
    void register_ShouldReturnOk() throws Exception {
        when(authService.register(any(RegisterRequestDto.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token"));
    }

    @Test
    void login_ShouldReturnOk() throws Exception {
        when(authService.login(any(LoginRequestDto.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token"));
    }

    @Test
    @WithMockUser(username = "john@example.com")
    void getProfileByEmail_ShouldReturnOk() throws Exception {
        when(authService.getProfileByEmail("john@example.com")).thenReturn(userDto);

        mockMvc.perform(get("/api/auth/profile/john@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void findAll_ShouldReturnList() throws Exception {
        when(authService.findAll()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/auth/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/auth/1"))
                .andExpect(status().isNoContent());
    }
}
