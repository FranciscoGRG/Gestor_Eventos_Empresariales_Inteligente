package com.gestor_empresarial.notification_service.email;

public interface EmailSender {
    boolean send(String to, String subject, String body);
}
