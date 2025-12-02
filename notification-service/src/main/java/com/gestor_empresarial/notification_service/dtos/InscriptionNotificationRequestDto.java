package com.gestor_empresarial.notification_service.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class InscriptionNotificationRequestDto extends NotificationRequestDto {
    private String eventName;
    private LocalDateTime eventDate;
    private String location;
}
