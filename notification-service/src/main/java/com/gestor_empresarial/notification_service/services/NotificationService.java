package com.gestor_empresarial.notification_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gestor_empresarial.notification_service.dtos.InscriptionNotificationRequestDto;
import com.gestor_empresarial.notification_service.dtos.RegistrationNotificationRequestDto;
import com.gestor_empresarial.notification_service.dtos.ReminderNotificationRequestDto;
import com.gestor_empresarial.notification_service.email.EmailSender;

@Service
public class NotificationService {

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private NotificationTemplateService templateService;

    public void sendRegistrationNotification(RegistrationNotificationRequestDto request) {
        emailSender.send(request.getUserEmail(), "Bienvenido a E-Event Manager",
                templateService.buildRegistrationEmail(request));
    }

    public void sendInscriptionNotification(InscriptionNotificationRequestDto request) {
        emailSender.send(request.getUserEmail(), "Inscripcion confirmada para el evento: " + request.getEventName(),
                templateService.buildInscriptionEmail(request));
    }

    public void sendReminderNotification(ReminderNotificationRequestDto request) {
        emailSender.send(request.getUserEmail(), "Recordatorio del evento: " + request.getEventName(), templateService.buildReminderEmail(request));
    }

}
