package com.gestor_empresarial.event_service.dtos;

import java.time.LocalDateTime;

public record EventResponseDto(
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
