package com.gestor_empresarial.event_service.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gestor_empresarial.event_service.dtos.EventRequestDto;
import com.gestor_empresarial.event_service.dtos.EventResponseDto;

import com.gestor_empresarial.event_service.dtos.EventUpdateRequestDto;
import com.gestor_empresarial.event_service.enums.Status;
import com.gestor_empresarial.event_service.exceptions.EventNotAvailableException;
import com.gestor_empresarial.event_service.exceptions.EventNotFoundException;
import com.gestor_empresarial.event_service.mappers.EventMapper;
import com.gestor_empresarial.event_service.models.Event;
import com.gestor_empresarial.event_service.repositories.IEventRepository;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private IEventRepository repository;

    @Mock
    private EventMapper mapper;

    @InjectMocks
    private EventService eventService;

    private Event event;
    private EventRequestDto eventRequest;
    private EventResponseDto eventResponse;
    private EventUpdateRequestDto updateRequest;

    @BeforeEach
    void setUp() {
        event = new Event();
        event.setId(1L);
        event.setOrganizerId(1L);
        event.setTitle("Test Event");
        event.setStatus(Status.ACTIVE);
        event.setCapacity(100);
        event.setNumRegistered(0);
        event.setPublished(true);

        eventRequest = new EventRequestDto("Test Event", "Description", "Location", LocalDateTime.now(),
                LocalDateTime.now().plusHours(2), 100, true);
        eventResponse = new EventResponseDto(1L, "Test Event", "Description", 1L, "Location", LocalDateTime.now(),
                LocalDateTime.now().plusHours(2), 100, 0, true, "ACTIVE");
        updateRequest = new EventUpdateRequestDto("Updated Event", "Description", "Location", LocalDateTime.now(),
                LocalDateTime.now().plusHours(2), 100, true);
    }

    @Test
    void createEvent_ShouldReturnResponse() {
        when(mapper.toEntity(any(EventRequestDto.class))).thenReturn(event);
        when(repository.save(any(Event.class))).thenReturn(event);
        when(mapper.toResponseDto(any(Event.class))).thenReturn(eventResponse);

        EventResponseDto response = eventService.createEvent(eventRequest, 1L);

        assertNotNull(response);
        assertEquals("Test Event", response.title());
        verify(repository).save(any(Event.class));
    }

    @Test
    void getAllEventsByOrganizerId_ShouldReturnList() {
        when(repository.findByOrganizerId(1L)).thenReturn(List.of(event));
        when(mapper.toResponseDto(any(Event.class))).thenReturn(eventResponse);

        List<EventResponseDto> responses = eventService.getAllEventsByOrganizerId(1L);

        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
    }

    @Test
    void deleteEvent_ShouldDelete_WhenExistsAndOwnerMatches() {
        when(repository.findByIdAndOrganizerId(1L, 1L)).thenReturn(Optional.of(event));

        eventService.deleteEvent(1L, 1L);

        verify(repository).delete(event);
    }

    @Test
    void deleteEvent_ShouldThrowException_WhenEventNotFound() {
        when(repository.findByIdAndOrganizerId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> eventService.deleteEvent(1L, 1L));
    }

    @Test
    void updateEvent_ShouldUpdate_WhenValid() {
        when(repository.findByIdAndOrganizerId(1L, 1L)).thenReturn(Optional.of(event));
        when(repository.save(any(Event.class))).thenReturn(event);
        when(mapper.toResponseDto(any(Event.class))).thenReturn(eventResponse);

        EventResponseDto response = eventService.updateEvent(1L, 1L, updateRequest);

        assertNotNull(response);
    }

    @Test
    void reserveCapacity_ShouldIncreaseRegistered_WhenAvailable() {
        when(repository.findById(1L)).thenReturn(Optional.of(event));

        eventService.reserveCapacity(1L);

        assertEquals(1, event.getNumRegistered());
        verify(repository).save(event);
    }

    @Test
    void reserveCapacity_ShouldThrowException_WhenFull() {
        event.setNumRegistered(100);
        when(repository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(EventNotAvailableException.class, () -> eventService.reserveCapacity(1L));
    }
}
