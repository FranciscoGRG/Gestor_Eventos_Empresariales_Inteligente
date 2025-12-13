package com.gestor_empresarial.notification_service.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.gestor_empresarial.notification_service.dtos.InscriptionNotificationRequestDto;
import com.gestor_empresarial.notification_service.dtos.RegistrationNotificationRequestDto;
import com.gestor_empresarial.notification_service.dtos.ReminderNotificationRequestDto;

@ExtendWith(MockitoExtension.class)
class NotificationTemplateServiceTest {

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private NotificationTemplateService templateService;

    @Test
    void buildRegistrationEmail_ShouldReturnContent() {
        RegistrationNotificationRequestDto request = new RegistrationNotificationRequestDto();
        request.setUserName("User");
        request.setUserEmail("user@example.com");
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("html-content");

        String content = templateService.buildRegistrationEmail(request);
        assertEquals("html-content", content);
    }

    @Test
    void buildInscriptionEmail_ShouldReturnContent() {
        InscriptionNotificationRequestDto request = new InscriptionNotificationRequestDto();
        request.setUserName("User");
        request.setUserEmail("user@example.com");
        request.setEventName("Event");
        request.setEventDate(java.time.LocalDateTime.now());
        request.setLocation("Loc");
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("html-content");

        String content = templateService.buildInscriptionEmail(request);
        assertEquals("html-content", content);
    }

    @Test
    void buildReminderEmail_ShouldReturnContent() {
        ReminderNotificationRequestDto request = new ReminderNotificationRequestDto();
        request.setUserName("User");
        request.setUserEmail("user@example.com");
        request.setEventName("Event");
        request.setEventDate(java.time.LocalDateTime.now());
        request.setLocation("Loc");
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("html-content");

        String content = templateService.buildReminderEmail(request);
        assertEquals("html-content", content);
    }
}
