package com.gestor_empresarial.registration_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.gestor_empresarial.registration_service.dtos.RegistrationNotificationDto;

@FeignClient(name = "notification-service")
public interface INotificationFeignClient {
    @PostMapping("/api/notifications/inscription")
    ResponseEntity<Void> sendInscriptionNotification(@RequestBody RegistrationNotificationDto request);
}
