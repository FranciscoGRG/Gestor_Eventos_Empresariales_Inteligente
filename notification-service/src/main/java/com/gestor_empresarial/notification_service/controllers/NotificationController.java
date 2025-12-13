package com.gestor_empresarial.notification_service.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestor_empresarial.notification_service.dtos.InscriptionNotificationRequestDto;
import com.gestor_empresarial.notification_service.dtos.RegistrationNotificationRequestDto;
import com.gestor_empresarial.notification_service.dtos.ReminderNotificationRequestDto;
import com.gestor_empresarial.notification_service.services.NotificationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @PostMapping("/registration")
    public ResponseEntity<Void> sendRegistrationNotification(@RequestBody RegistrationNotificationRequestDto request) {
        service.sendRegistrationNotification(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/inscription")
    public ResponseEntity<Void> sendInscriptionNotification(@RequestBody InscriptionNotificationRequestDto request) {
        service.sendInscriptionNotification(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reminder")
    public ResponseEntity<Void> sendReminderNotification(@RequestBody ReminderNotificationRequestDto request) {
        service.sendReminderNotification(request);
        return ResponseEntity.noContent().build();
    }
}
