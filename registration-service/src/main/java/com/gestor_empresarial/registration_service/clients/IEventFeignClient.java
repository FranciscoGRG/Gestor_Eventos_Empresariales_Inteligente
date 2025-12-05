package com.gestor_empresarial.registration_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.gestor_empresarial.registration_service.dtos.EventDto;

@FeignClient(name = "events-service")
public interface IEventFeignClient {
    @PostMapping("/api/events/register/{id}")
    ResponseEntity<Void> reserveCapacityAndRegister(@PathVariable Long id, @RequestHeader("X-User-ID") Long userId);

    @PostMapping("/api/events/release/{id}")
    ResponseEntity<Void> releaseCapacity(@PathVariable Long id, @RequestHeader("X-User-ID") Long userId);

    @GetMapping("/api/events/{id}")
    ResponseEntity<EventDto> getEventById(@PathVariable Long id);
}
