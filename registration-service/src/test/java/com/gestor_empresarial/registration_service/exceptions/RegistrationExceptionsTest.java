package com.gestor_empresarial.registration_service.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class RegistrationExceptionsTest {

    @Test
    void capacityErrorException_ShouldSetMessage() {
        String msg = "Error message";
        CapacityErrorException exception = new CapacityErrorException(msg);
        assertEquals(msg, exception.getMessage());
    }

    @Test
    void capacityErrorException_ShouldBeThrown() {
        assertThrows(CapacityErrorException.class, () -> {
            throw new CapacityErrorException("Error");
        });
    }

    @Test
    void eventNotAvailableException_ShouldSetMessage() {
        String msg = "Not available";
        EventNotAvailableException exception = new EventNotAvailableException(msg);
        assertEquals(msg, exception.getMessage());
    }
}
