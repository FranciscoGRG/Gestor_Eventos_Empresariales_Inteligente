package com.gestor_empresarial.event_service.mappers;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.gestor_empresarial.event_service.dtos.EventRequestDto;
import com.gestor_empresarial.event_service.dtos.EventResponseDto;
import com.gestor_empresarial.event_service.enums.Status;
import com.gestor_empresarial.event_service.models.Event;

class EventMapperTest {

    private EventMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new EventMapper();
    }

    @Test
    void toEntity_ShouldMapCorrectly() {
        EventRequestDto request = new EventRequestDto("Title", "Desc", "Loc",
                LocalDateTime.now(), LocalDateTime.now().plusHours(2), 100, true);

        Event event = mapper.toEntity(request);

        assertNotNull(event);
        assertEquals("Title", event.getTitle());
        assertEquals("Desc", event.getDescription());
        assertEquals("Loc", event.getLocation());
        assertEquals(100, event.getCapacity());
        assertTrue(event.isPublished());
        assertEquals(Status.ACTIVE, event.getStatus());
    }

    @Test
    void toResponseDto_ShouldMapCorrectly() {
        Event event = new Event();
        event.setId(1L);
        event.setTitle("Title");
        event.setDescription("Desc");
        event.setOrganizerId(1L);
        event.setLocation("Loc");
        event.setStartDate(LocalDateTime.now());
        event.setEndDate(LocalDateTime.now().plusHours(2));
        event.setCapacity(100);
        event.setNumRegistered(10);
        event.setPublished(true);
        event.setStatus(Status.ACTIVE);

        EventResponseDto response = mapper.toResponseDto(event);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Title", response.title());
        assertEquals(1L, response.organizerId());
        assertEquals("ACTIVE", response.status());
    }
}
