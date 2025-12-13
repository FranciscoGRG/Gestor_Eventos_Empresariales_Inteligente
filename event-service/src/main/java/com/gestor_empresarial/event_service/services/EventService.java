package com.gestor_empresarial.event_service.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestor_empresarial.event_service.dtos.EventRequestDto;
import com.gestor_empresarial.event_service.dtos.EventResponseDto;
import com.gestor_empresarial.event_service.dtos.EventStatusUpdateDTO;
import com.gestor_empresarial.event_service.dtos.EventUpdateRequestDto;
import com.gestor_empresarial.event_service.enums.Status;
import com.gestor_empresarial.event_service.exceptions.EventNotAvailableException;
import com.gestor_empresarial.event_service.exceptions.EventNotFoundException;
import com.gestor_empresarial.event_service.exceptions.EventOrganizerIdDoesntMatchException;
import com.gestor_empresarial.event_service.mappers.EventMapper;
import com.gestor_empresarial.event_service.models.Event;
import com.gestor_empresarial.event_service.repositories.IEventRepository;

@Service
public class EventService {

    private final IEventRepository repository;

    private final EventMapper mapper;

    public EventService(IEventRepository repository, EventMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public EventResponseDto createEvent(EventRequestDto request, Long id) {
        Event event = mapper.toEntity(request);
        event.setOrganizerId(id);
        event.setNumRegistered(0);
        Event createdEvent = repository.save(event);

        return mapper.toResponseDto(createdEvent);
    }

    public List<EventResponseDto> getAllEventsByOrganizerId(Long organizerId) {
        List<Event> events = repository.findByOrganizerId(organizerId);

        return events.stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    public List<EventResponseDto> findAllPublishedAndActiveEvents() {
        List<Event> events = repository.findByStatusAndIsPublishedTrue(Status.ACTIVE);

        return events.stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    @Transactional
    public void deleteEvent(Long id, Long userId) {
        Event event = repository.findByIdAndOrganizerId(id, userId)
                .orElseThrow(
                        () -> new EventNotFoundException(id));

        if (userId != event.getOrganizerId()) {
            throw new EventOrganizerIdDoesntMatchException();
        }

        repository.delete(event);
    }

    @Transactional
    public EventResponseDto updateEvent(Long id, Long userId, EventUpdateRequestDto request) {
        Event event = repository.findByIdAndOrganizerId(id, userId)
                .orElseThrow(
                        () -> new EventNotFoundException(id));

        if (userId != event.getOrganizerId()) {
            throw new EventOrganizerIdDoesntMatchException();
        }

        if (event.getStatus() == Status.COMPLETED) {
            throw new EventNotAvailableException(id);
        }

        if (event.getStatus() == Status.ACTIVE && request.capacity() < event.getCapacity() && event.getNumRegistered() > request.capacity()) {
            throw new EventNotAvailableException(id);
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
                .orElseThrow(() -> new EventNotFoundException(id));

        if (!event.isPublished() || event.getStatus() != Status.ACTIVE) {
            throw new EventNotAvailableException(id);
        }

        return mapper.toResponseDto(event);
    }

    public List<EventResponseDto> findPublishedEventsByTitle(String title) {
        List<Event> events = repository.findByTitleContainingIgnoreCaseAndIsPublishedTrue(title);

        return events.stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    @Transactional
    public EventResponseDto updateEventStatus(Long id, Long userId, EventStatusUpdateDTO request) {
        Event event = repository.findByIdAndOrganizerId(id, userId)
                .orElseThrow(() -> new EventNotFoundException(id));

        if (userId != event.getOrganizerId()) {
            throw new EventOrganizerIdDoesntMatchException();
        }

        if (event.getStatus() == Status.COMPLETED) {
            throw new EventNotAvailableException(id);
        }

        event.setStatus(request.newStatus());

        Event eventUpdated = repository.save(event);
        return mapper.toResponseDto(eventUpdated);
    }

    @Transactional
    public void reserveCapacity(Long eventId) {
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        if (event.getStatus() != Status.ACTIVE) {
            throw new EventNotAvailableException(eventId);
        }

        if (event.getNumRegistered() >= event.getCapacity()) {
            throw new EventNotAvailableException(eventId);
        }

        event.setNumRegistered(event.getNumRegistered() + 1);
        repository.save(event);
    }

    @Transactional
    public void releaseCapacity(Long eventId) {
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        if (event.getStatus() != Status.ACTIVE) {
            throw new EventNotAvailableException(eventId);
        }

        if (event.getNumRegistered() <= 0) {
            throw new EventNotAvailableException(eventId);
        }

        event.setNumRegistered(event.getNumRegistered() - 1);
        repository.save(event);
    }

}