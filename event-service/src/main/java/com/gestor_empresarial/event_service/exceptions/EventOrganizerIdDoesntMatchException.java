package com.gestor_empresarial.event_service.exceptions;

public class EventOrganizerIdDoesntMatchException extends RuntimeException {

    public static final String EVENT_ORGANIZER_ID_DOESNT_MATCH = "El id del organizador y del usuario logeado no coinciden";

    public EventOrganizerIdDoesntMatchException() {
        super(EVENT_ORGANIZER_ID_DOESNT_MATCH);
    }
}
