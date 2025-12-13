package com.gestor_empresarial.event_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EventNotAvailableException extends RuntimeException {

    public static final String EVENT_NOT_AVAILABLE = "El evento con id: %s no esta disponible";

    public EventNotAvailableException(Long eventId) {
        super(String.format(EVENT_NOT_AVAILABLE, eventId));
    }
}