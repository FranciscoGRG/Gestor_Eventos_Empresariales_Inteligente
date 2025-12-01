package com.gestor_empresarial.registration_service.dtos;


import com.gestor_empresarial.registration_service.enums.RegistrationStatus;

public record RegistrationResponseDto(
    Long id,
    Long userId,
    Long eventId,
    RegistrationStatus status
) {}
