package com.gestor_empresarial.event_service.dtos;

import com.gestor_empresarial.event_service.enums.Status;

public record EventStatusUpdateDTO(
    Status newStatus
) {}
