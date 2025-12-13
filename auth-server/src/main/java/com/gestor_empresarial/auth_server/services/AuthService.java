package com.gestor_empresarial.auth_server.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gestor_empresarial.auth_server.dtos.AuthResponseDto;
import com.gestor_empresarial.auth_server.dtos.LoginRequestDto;
import com.gestor_empresarial.auth_server.dtos.RegisterRequestDto;
import com.gestor_empresarial.auth_server.dtos.UserDto;
import com.gestor_empresarial.auth_server.dtos.UserNotificationDto;
import com.gestor_empresarial.auth_server.dtos.UserRoleUpdateDto;
import com.gestor_empresarial.auth_server.enums.Role;
import com.gestor_empresarial.auth_server.exceptions.BadCredentialsException;
import com.gestor_empresarial.auth_server.exceptions.UserAlreadyExists;
import com.gestor_empresarial.auth_server.exceptions.UserNotFoundException;
import com.gestor_empresarial.auth_server.models.User;
import com.gestor_empresarial.auth_server.repositories.IUserRepository;

@Service
public class AuthService {

    private static final String USER_NOT_FOUNT_MSG = "Usuario no encontrado";
    private static final String BAD_CREDENTIALS_MSG = "Credenciales incorrectas";
    private static final String USER_ALREADY_EXISTS_MSG = "El usuario ya existe";

    @Autowired
    private IUserRepository repository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public List<User> findAll() {
        return repository.findAll();
    }

    public AuthResponseDto register(RegisterRequestDto request) {
        if (repository.existsByEmail(request.email())) {
            throw new UserAlreadyExists(USER_ALREADY_EXISTS_MSG);
        }

        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRoles(Set.of(Role.ROLE_USER));

        User userSaved = repository.save(user);

        String token = jwtService.generateToken(userSaved.getId(), userSaved.getEmail(), userSaved.getRoles());

        UserNotificationDto userNotification = new UserNotificationDto(userSaved.getFirstName(), userSaved.getEmail());
        kafkaTemplate.send("user-created", userNotification);

        return new AuthResponseDto(token, userSaved.getEmail(), userSaved.getRoles());
    }

    public AuthResponseDto login(LoginRequestDto request) {
        User user = repository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUNT_MSG));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException(BAD_CREDENTIALS_MSG);
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRoles());
        return new AuthResponseDto(token, user.getEmail(), user.getRoles());
    }

    public User getProfile(String email) {
        return repository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUNT_MSG));
    }

    public UserDto getProfileByEmail(String email) {
        User user = repository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUNT_MSG));

        return mapUserToDto(user);
    }

    public void deleteUser(Long id) {
        User user = repository.findById(id).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUNT_MSG));
        repository.delete(user);
    }

    public UserNotificationDto getUserNameAndEmail(Long id) {
        User user = repository.findById(id).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUNT_MSG));

        UserNotificationDto userNotification = new UserNotificationDto(
                user.getFirstName(),
                user.getEmail());

        return userNotification;
    }

    public void updatedUserRole(Long id, UserRoleUpdateDto updatedUser) {
        User existingUser = repository.findById(id).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUNT_MSG));

        Role newRole;

        try {
            newRole = Role.valueOf(updatedUser.newRole());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("El rol especificado no es válido");
        }

        existingUser.getRoles().add(newRole);

        repository.save(existingUser);
    }

    public Optional<Long> findIdByEmail(String email) {
        return repository.findIdByEmail(email);
    }

    private UserDto mapUserToDto(User user) {
       
        Set<String> roleNames = user.getRoles().stream()
                .map(role -> role.name())
                .collect(Collectors.toSet()); 

        return new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                roleNames
        );
    }

}
