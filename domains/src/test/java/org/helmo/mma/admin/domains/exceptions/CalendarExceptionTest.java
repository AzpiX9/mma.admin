package org.helmo.mma.admin.domains.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalendarExceptionTest {

    @Test
    public void should_return_a_given_message_when_throw_exception() {
        String message = "Une erreur est survenue dans le calendrier";
        CalendarException exception = assertThrows(CalendarException.class, () -> {
            throw new CalendarException(message);
        });

        assertEquals("CalendarException -> Une erreur est survenue dans le calendrier", exception.getMessage());
    }
}