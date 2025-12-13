package com.gestor_empresarial.notification_service.services;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gestor_empresarial.notification_service.dtos.InscriptionNotificationRequestDto;
import com.gestor_empresarial.notification_service.dtos.RegistrationNotificationRequestDto;
import com.gestor_empresarial.notification_service.dtos.ReminderNotificationRequestDto;
import com.gestor_empresarial.notification_service.email.EmailSender;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private EmailSender emailSender;

    @Mock
    private NotificationTemplateService templateService;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void sendRegistrationNotification_ShouldSendEmail() {
        RegistrationNotificationRequestDto request = new RegistrationNotificationRequestDto();
        request.setUserName("User");
        request.setUserEmail("user@example.com");

        when(templateService.buildRegistrationEmail(request)).thenReturn("html-content");

        notificationService.sendRegistrationNotification(request);

        verify(emailSender).send(eq("user@example.com"), anyString(), eq("html-content"));
    }

    @Test
    void sendInscriptionNotification_ShouldSendEmail() {
        InscriptionNotificationRequestDto request = new InscriptionNotificationRequestDto();
        request.setUserName("User");
        request.setUserEmail("user@example.com");
        request.setEventName("Event");
        request.setEventDate(java.time.LocalDateTime.now());
        request.setLocation("Loc");

        when(templateService.buildInscriptionEmail(request)).thenReturn("html-content");

        notificationService.sendInscriptionNotification(request);

        verify(emailSender).send(eq("user@example.com"), anyString(), eq("html-content"));
    }

    @Test
    void sendReminderNotification_ShouldSendEmail() {
        ReminderNotificationRequestDto request = new ReminderNotificationRequestDto();
        request.setUserName("User");
        request.setUserEmail("user@example.com");
        request.setEventName("Event");
        request.setEventDate(java.time.LocalDateTime.now());
        request.setLocation("Loc");

        when(templateService.buildReminderEmail(request)).thenReturn("html-content");

        notificationService.sendReminderNotification(request);

        verify(emailSender).send(eq("user@example.com"), anyString(), eq("html-content"));
    }
}
