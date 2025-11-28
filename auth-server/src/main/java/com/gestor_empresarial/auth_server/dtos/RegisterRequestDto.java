package com.gestor_empresarial.auth_server.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(
    @NotBlank(message = "El email es obligatorio") @Email(message = "El formato del email no es válido") String email,

    @NotBlank(message = "La contraseña es obligatoria") @Size(min = 6, max = 50, message = "La contraseña debe tener entre 6 y 50 caracteres") String password,

    @NotBlank(message = "El nombre es obligatorio") String firstName,

    @NotBlank(message = "El apellido es obligatorio") String lastName) {
}