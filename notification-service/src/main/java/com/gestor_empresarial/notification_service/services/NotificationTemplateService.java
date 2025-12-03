package com.gestor_empresarial.notification_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.gestor_empresarial.notification_service.dtos.InscriptionNotificationRequestDto;
import com.gestor_empresarial.notification_service.dtos.RegistrationNotificationRequestDto;
import com.gestor_empresarial.notification_service.dtos.ReminderNotificationRequestDto;

@Service
public class NotificationTemplateService {
    @Autowired
    TemplateEngine templateEngine;

    public String buildRegistrationEmail(RegistrationNotificationRequestDto request) {
        Context ctx = new Context();
        ctx.setVariable("name", request.getUserName());
        return templateEngine.process("emails/registration.html", ctx);
    }

    public String buildInscriptionEmail(InscriptionNotificationRequestDto request) {
        Context ctx = new Context();
        ctx.setVariable("name", request.getUserName());
        ctx.setVariable("eventName", request.getEventName());
        ctx.setVariable("eventDate", request.getEventDate());
        ctx.setVariable("eventLocation", request.getLocation());
        return templateEngine.process("emails/inscription.html", ctx);
    }

    public String buildReminderEmail(ReminderNotificationRequestDto request) {
        Context ctx = new Context();
        ctx.setVariable("name", request.getUserName());
        ctx.setVariable("eventName", request.getEventName());
        ctx.setVariable("eventDate", request.getEventDate());
        return templateEngine.process("emails/reminder.html", ctx);
    }
}
