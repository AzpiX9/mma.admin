package org.helmo.mma.admin.domains.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DirExceptionTest {
    @Test
    public void should_return_a_given_message_when_throw_exception() {
        String message = "Répertoire invalide";
        DirException exception = assertThrows(DirException.class, () -> {
            throw new DirException(message);
        });

        assertEquals("DirException -> Répertoire invalide", exception.getMessage());
    }
}