package com.gestor_empresarial.auth_server.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestor_empresarial.auth_server.dtos.AuthResponseDto;
import com.gestor_empresarial.auth_server.dtos.LoginRequestDto;
import com.gestor_empresarial.auth_server.dtos.RegisterRequestDto;
import com.gestor_empresarial.auth_server.dtos.UserDto;
import com.gestor_empresarial.auth_server.dtos.UserNotificationDto;
import com.gestor_empresarial.auth_server.dtos.UserRoleUpdateDto;
import com.gestor_empresarial.auth_server.models.User;
import com.gestor_empresarial.auth_server.services.AuthService;


@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody RegisterRequestDto request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/profile")
    public ResponseEntity<User> profile(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(authService.getProfile(userDetails.getUsername()));
    }

    @GetMapping("/profile/{email}")
    public ResponseEntity<UserDto> getProfileByEmail(@PathVariable String email) {
        return ResponseEntity.ok(authService.getProfileByEmail(email));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            authService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<User>> findAll() {
        return ResponseEntity.ok(authService.findAll());
    }

    @GetMapping("/getUserEmailAndName/{id}")
    public ResponseEntity<UserNotificationDto> getUserEmailAndName(@PathVariable Long id) {
        return ResponseEntity.ok(authService.getUserNameAndEmail(id));
    }

    @PutMapping("/updateRole")
    public ResponseEntity<Void> updateUserRole(@RequestHeader("X-User-ID") Long userId, @RequestBody UserRoleUpdateDto newRole) {
        authService.updatedUserRole(userId, newRole);
        return ResponseEntity.noContent().build();
    }
    
}
