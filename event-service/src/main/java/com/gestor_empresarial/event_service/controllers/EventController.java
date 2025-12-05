package com.gestor_empresarial.event_service.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestor_empresarial.event_service.dtos.EventRequestDto;
import com.gestor_empresarial.event_service.dtos.EventResponseDto;
import com.gestor_empresarial.event_service.dtos.EventStatusUpdateDTO;
import com.gestor_empresarial.event_service.dtos.EventUpdateRequestDto;
import com.gestor_empresarial.event_service.services.EventService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService service;

    @GetMapping("/title/{name}")
    public ResponseEntity<List<EventResponseDto>> getPublishedEventsByTitle(@PathVariable String name) {
        return ResponseEntity.ok(service.findPublishedEventsByTitle(name));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDto> getPublishedEventById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findPublishedEventById(id));
    }

    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<List<EventResponseDto>> getEventsByOrganizerId(@PathVariable Long organizerId) {
        return ResponseEntity.ok(service.getAllEventsByOrganizerId(organizerId));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<EventResponseDto>> getCreatedEventsByLoggedUser(
            @RequestHeader("X-User-ID") Long userId) {
        return ResponseEntity.ok(service.getAllEventsByOrganizerId(userId));
    }

    @PostMapping()
    public ResponseEntity<EventResponseDto> createEvent(@RequestBody EventRequestDto request,
            @RequestHeader("X-User-ID") Long userId) {
        return ResponseEntity.ok(service.createEvent(request, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDto> updateEvent(@PathVariable long id,
            @RequestBody EventUpdateRequestDto request, @RequestHeader("X-User-ID") Long userId) {
        return ResponseEntity.ok(service.updateEvent(id, userId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id, @RequestHeader("X-User-ID") Long userId) {
        service.deleteEvent(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<EventResponseDto> updateEventStatus(@PathVariable Long id,
            @RequestHeader("X-User-ID") Long userId, @RequestBody EventStatusUpdateDTO request) {
        return ResponseEntity.ok(service.updateEventStatus(id, userId, request));
    }

    @PostMapping("/register/{id}")
    public ResponseEntity<Void> reserveCapacityAndRegister(@PathVariable Long id, @RequestHeader("X-User-ID") Long userId) {
        service.reserveCapacity(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/release/{id}")
    public ResponseEntity<Void> releaseCapacity(@PathVariable Long id, @RequestHeader("X-User-ID") Long userId) {
        service.releaseCapacity(id);
        return ResponseEntity.noContent().build();
    }
    
}
