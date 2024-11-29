package org.helmo.mma.admin.domains.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoomExceptionTest {
    @Test
    public void should_return_a_given_message_when_throw_exception() {
        String message = "Salle non valide";
        RoomException exception = assertThrows(RoomException.class, () -> {
            throw new RoomException(message);
        });

        assertEquals("Salle non valide", exception.getMessage());
    }
}