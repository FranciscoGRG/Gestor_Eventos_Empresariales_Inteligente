package com.gestor_empresarial.event_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EventNotFoundException extends RuntimeException {

    public static final String EVENT_NOT_FOUND = "El evento con id: %s no existe o no se encuentra";

    public EventNotFoundException(Long eventId) {
        super(String.format(EVENT_NOT_FOUND, eventId));
    }
}
