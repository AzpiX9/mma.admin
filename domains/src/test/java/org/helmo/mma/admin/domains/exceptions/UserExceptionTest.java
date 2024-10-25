package org.helmo.mma.admin.domains.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserExceptionTest {
    @Test
    public void should_return_a_given_message_when_throw_exception() {
        String message = "Utilisateur non valide";
        UserException exception = assertThrows(UserException.class, () -> {
            throw new UserException(message);
        });

        assertEquals("UserException -> Utilisateur non valide", exception.getMessage());
    }
}