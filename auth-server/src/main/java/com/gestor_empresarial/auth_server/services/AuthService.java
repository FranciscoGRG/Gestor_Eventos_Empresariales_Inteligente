package com.gestor_empresarial.auth_server.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gestor_empresarial.auth_server.clients.INotificationFeignClient;
import com.gestor_empresarial.auth_server.dtos.AuthResponseDto;
import com.gestor_empresarial.auth_server.dtos.LoginRequestDto;
import com.gestor_empresarial.auth_server.dtos.RegisterRequestDto;
import com.gestor_empresarial.auth_server.dtos.UserDto;
import com.gestor_empresarial.auth_server.dtos.UserNotificationDto;
import com.gestor_empresarial.auth_server.dtos.UserRoleUpdateDto;
import com.gestor_empresarial.auth_server.enums.Role;
import com.gestor_empresarial.auth_server.models.User;
import com.gestor_empresarial.auth_server.repositories.IUserRepository;

@Service
public class AuthService {

    @Autowired
    private IUserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private INotificationFeignClient notificationFeignClient;

    @Autowired
    private JwtService jwtService;

    public List<User> findAll() {
        return repository.findAll();
    }

    public AuthResponseDto register(RegisterRequestDto request) {
        if (repository.existsByEmail(request.email())) {
            throw new RuntimeException("El email ya esta en uso");
        }

        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRoles(Set.of(Role.ROLE_USER));

        User userSaved = repository.save(user);

        String token = jwtService.generateToken(userSaved.getId(), userSaved.getEmail(), userSaved.getRoles());

        notificationFeignClient
                .sendRegistrationNotification(new UserNotificationDto(userSaved.getFirstName(), userSaved.getEmail()));
        return new AuthResponseDto(token, userSaved.getEmail(), userSaved.getRoles());
    }

    public AuthResponseDto login(LoginRequestDto request) {
        User user = repository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Usuario o contraseña incorrectos");
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRoles());
        return new AuthResponseDto(token, user.getEmail(), user.getRoles());
    }

    public User getProfile(String email) {
        return repository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public UserDto getProfileByEmail(String email) {
        User user = repository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return mapUserToDto(user);
    }

    public void deleteUser(Long id) {
        User user = repository.findById(id).orElseThrow(() -> new RuntimeException("El user no existe"));
        repository.delete(user);
    }

    public UserNotificationDto getUserNameAndEmail(Long id) {
        User user = repository.findById(id).orElseThrow(() -> new RuntimeException("El user no existe"));

        UserNotificationDto userNotification = new UserNotificationDto(
                user.getFirstName(),
                user.getEmail());

        return userNotification;
    }

    public void updatedUserRole(Long id, UserRoleUpdateDto updatedUser) {
        User existingUser = repository.findById(id).orElseThrow(() -> new RuntimeException("El user no existe"));

        Role newRole;

        try {
            newRole = Role.valueOf(updatedUser.newRole());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("El rol especificado no es válido");
        }

        existingUser.getRoles().add(newRole);

        repository.save(existingUser);
    }

    public Optional<Long> findIdByEmail(String email) {
        return repository.findIdByEmail(email);
    }

    private UserDto mapUserToDto(User user) {
        // Asegúrate de que tu UserDto acepta los campos que estás enviando (id, name,
        // email, roles(Set<String>))

        // 1. Convertir Set<Role> a Set<String>
        Set<String> roleNames = user.getRoles().stream()
                .map(role -> role.name())
                .collect(Collectors.toSet()); // Necesitas importar java.util.stream.Collectors

        return new UserDto(
                user.getId(), // Asumiendo que UserDto tiene Long id
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                roleNames // El campo Set<String>
        );
    }

}
