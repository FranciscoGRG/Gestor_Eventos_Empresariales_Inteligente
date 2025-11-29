package com.gestor_empresarial.event_service.mappers;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.gestor_empresarial.event_service.dtos.EventRequestDto;
import com.gestor_empresarial.event_service.dtos.EventResponseDto;
import com.gestor_empresarial.event_service.enums.Status;
import com.gestor_empresarial.event_service.models.Event;

@Component
public class EventMapper {
    public Event toEntity(EventRequestDto request) {
        LocalDateTime now = LocalDateTime.now();

        return new Event(
                null,
                request.title(),
                request.description(),
                null,
                request.location(),
                request.startDate(),
                request.endDate(),
                request.capacity(),
                0,
                request.isPublished(),
                Status.DRAFT,
                now,
                now);
    }

    public EventResponseDto toResponseDto(Event event) {
        return new EventResponseDto(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getOrganizerId(),
                event.getLocation(),
                event.getStartDate(),
                event.getEndDate(),
                event.getCapacity(),
                event.getNumRegistered(),
                event.isPublished(),
                event.getStatus().name());
    }
}
