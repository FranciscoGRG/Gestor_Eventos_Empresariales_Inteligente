package com.gestor_empresarial.event_service.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gestor_empresarial.event_service.dtos.EventRequestDto;
import com.gestor_empresarial.event_service.dtos.EventResponseDto;
import com.gestor_empresarial.event_service.dtos.EventUpdateRequestDto;
import com.gestor_empresarial.event_service.enums.Status;
import com.gestor_empresarial.event_service.mappers.EventMapper;
import com.gestor_empresarial.event_service.models.Event;
import com.gestor_empresarial.event_service.repositories.IEventRepository;

@Service
public class EventService {

    @Autowired
    private IEventRepository repository;

    @Autowired
    private EventMapper mapper;

    public EventResponseDto createEvent(EventRequestDto request, Long id) {
        Event event = mapper.toEntity(request);
        event.setOrganizerId(id);
        event.setNumRegistered(0);
        Event createdEvent = repository.save(event);

        return mapper.toResponseDto(createdEvent);
    }

    public List<EventResponseDto> getAllEventsByOrganizerId(Long organizerId) {
        List<Event> events = repository.findByOrganizerId(organizerId);

        List<EventResponseDto> responseDtos = events.stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());

        return responseDtos;
    }

    public void deleteEvent(Long id, Long userId) {
        Event event = repository.findByIdAndOrganizerId(id, userId)
                .orElseThrow(() -> new RuntimeException("El evento con id: " + id + " no existe o no se encuentra"));

        if (userId != event.getOrganizerId()) {
            throw new RuntimeException("El id del organizador y del usuario logeado no coinciden");
        }

        repository.delete(event);
    }

    public EventResponseDto updateEvent(Long id, Long userId, EventUpdateRequestDto request) {
        Event event = repository.findByIdAndOrganizerId(id, userId)
                .orElseThrow(() -> new RuntimeException("El evento con id: " + id + " no existe o no se encuentra"));

        if (userId != event.getOrganizerId()) {
            throw new RuntimeException("El id del organizador y del usuario logeado no coinciden");
        }

        if (event.getStatus() == Status.COMPLETED) {
            throw new RuntimeException("No se puede actualizar porque el evento ya ha sido completado");
        }

        if (event.getStatus() == Status.ACTIVE && request.capacity() < event.getCapacity()) {
            if (event.getNumRegistered() > request.capacity()) {
                throw new RuntimeException("La capacidad no puede ser menor al numero de personas inscritas al evento: "
                        + event.getNumRegistered());
            }
        }

        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setLocation(request.location());
        event.setStartDate(request.startDate());
        event.setEndDate(request.endDate());
        event.setCapacity(request.capacity());
        event.setPublished(request.isPublished());

        Event eventUpdated = repository.save(event);
        return mapper.toResponseDto(eventUpdated);
    }

    public EventResponseDto findPublishedEventById(Long id) {
        Event event = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se ha encontrado el evento con id: " + id));

        if (!event.isPublished() || event.getStatus() != Status.ACTIVE) {
            throw new RuntimeException("El evento con id: " + id + " no esta disponible");
        }

        return mapper.toResponseDto(event);
    }

    public List<EventResponseDto> findPublishedEventsByTitle(String title) {
        List<Event> events = repository.findByTitleContainingIgnoreCaseAndIsPublishedTrue(title);

        List<EventResponseDto> responseDtos = events.stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());

        return responseDtos;
    }
}