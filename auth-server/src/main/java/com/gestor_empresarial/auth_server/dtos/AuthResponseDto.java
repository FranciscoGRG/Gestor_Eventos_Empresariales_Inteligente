package com.gestor_empresarial.auth_server.dtos;

import java.util.Set;

import com.gestor_empresarial.auth_server.enums.Role;


public record AuthResponseDto(
    String token,
    String email,
    Set<Role> roles
) {
}
