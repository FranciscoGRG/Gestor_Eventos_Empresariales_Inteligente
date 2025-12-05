package com.gestor_empresarial.registration_service.dtos;

import java.time.LocalDateTime;

public record RegistrationNotificationDto(
    String userEmail,
    String userName,
    String eventName,
    LocalDateTime eventDate,
    String location) {
}
