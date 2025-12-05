package com.gestor_empresarial.auth_server.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.gestor_empresarial.auth_server.dtos.UserNotificationDto;

@FeignClient(name = "notification-service")
public interface INotificationFeignClient {
    @PostMapping("/api/notifications/registration")
    public void sendRegistrationNotification(@RequestBody UserNotificationDto request);
}
