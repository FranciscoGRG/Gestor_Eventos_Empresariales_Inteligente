package com.gestor_empresarial.registration_service.dtos;

import java.time.LocalDateTime;

public record EventDto(
    Long id,
    String title,
    String description,
    Long organizerId,
    String location,
    LocalDateTime startDate,
    LocalDateTime endDate,
    Integer capacity,
    Integer numRegistered,
    boolean isPublished,
    String status
) {}
