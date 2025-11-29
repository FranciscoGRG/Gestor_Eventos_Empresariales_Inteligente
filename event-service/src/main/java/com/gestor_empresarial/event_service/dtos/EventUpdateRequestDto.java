package com.gestor_empresarial.event_service.dtos;

import java.time.LocalDateTime;

public record EventUpdateRequestDto(
    String title,
    String description,
    String location,
    LocalDateTime startDate,
    LocalDateTime endDate,
    Integer capacity,
    boolean isPublished) {
}
