package com.gestor_empresarial.event_service.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class EventExceptionsTest {

    @Test
    void eventDoentAvaliableException_ShouldSetMessage() {
        String msg = "Not available";
        EventDoentAvaliableException exception = new EventDoentAvaliableException(msg);
        assertEquals(msg, exception.getMessage());
    }

    @Test
    void eventOrganizerIdDoesntMatchException_ShouldSetDefaultMessage() {
        EventOrganizerIdDoesntMatchException exception = new EventOrganizerIdDoesntMatchException();
        assertEquals(EventOrganizerIdDoesntMatchException.EVENT_ORGANIZER_ID_DOESNT_MATCH, exception.getMessage());
    }
}
