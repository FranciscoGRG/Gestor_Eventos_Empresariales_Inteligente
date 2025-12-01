package com.gestor_empresarial.registration_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "event-service")
public interface IEventFeignClient {
    @PostMapping("/api/events/register/{id}")
    ResponseEntity<Void> reserveCapacityAndRegister(@PathVariable Long id);

    @PostMapping("/api/events/release/{id}")
    ResponseEntity<Void> releaseCapacity(@PathVariable Long id);
}
