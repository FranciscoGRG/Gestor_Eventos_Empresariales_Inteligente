package com.gestor_empresarial.notification_service.listeners;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.gestor_empresarial.notification_service.dtos.InscriptionNotificationRequestDto;
import com.gestor_empresarial.notification_service.dtos.RegistrationNotificationRequestDto;
import com.gestor_empresarial.notification_service.services.NotificationService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class NotificationListener {

    private final NotificationService service;

    public NotificationListener(NotificationService service) {
        this.service = service;
    }

    @KafkaListener(topics = "user-created", groupId = "notification-group")
    public void handleUserCreated(RegistrationNotificationRequestDto event) {
        service.sendRegistrationNotification(event);
    }

    // Escuchar cuando alguien se inscribe
    @KafkaListener(topics = "registration-event", groupId = "notification-group")
    public void handleEventEnrollment(InscriptionNotificationRequestDto event) {
        service.sendInscriptionNotification(event);
    }
}
