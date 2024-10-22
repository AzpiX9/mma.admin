package org.helmo.mma.admin.domains.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    @Test
    public void should_return_a_valid_user() {
        User user = new User("B23456", "Doe", "John", "john.doe@example.com");

        assertEquals("B23456", user.Matricule());
        assertEquals("Doe", user.Nom());
        assertEquals("John", user.Prenom());
        assertEquals("john.doe@example.com", user.Email());
    }

    @Test
    public void testInvalidMatricule() {
        assertThrows(IllegalArgumentException.class, () -> {
            new User("", "Doe", "John", "john.doe@example.com");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new User(null, "Doe", "John", "john.doe@example.com");
        });
    }

    @Test
    public void testInvalidNom() {
        assertThrows(IllegalArgumentException.class, () -> {
            new User("B23456", "", "John", "john.doe@example.com");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new User("B23456", null, "John", "john.doe@example.com");
        });
    }

    @Test
    public void testInvalidPrenom() {
        assertThrows(IllegalArgumentException.class, () -> {
            new User("B23456", "Doe", "", "john.doe@example.com");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new User("B23456", "Doe", null, "john.doe@example.com");
        });
    }

    @Test
    public void testInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            new User("B23456", "Doe", "John", "john.doeexample.com");  // Pas de "@" dans l'email
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new User("B23456", "Doe", "John", "");  // Email vide
        });
    }
}