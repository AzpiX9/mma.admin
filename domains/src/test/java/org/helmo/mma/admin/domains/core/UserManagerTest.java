package org.helmo.mma.admin.domains.core;

import org.helmo.mma.admin.domains.exceptions.UserException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserManagerTest {
    @Test
    void shouldReturnUserWhenMatriculeExists() {
        // Arrange
        List<User> users = List.of(
                new User("A123456", "Smith", "John", "john.smith@example.com"),
                new User("B789101", "Doe", "Jane", "jane.doe@example.com")
        );
        UserManager manager = new UserManager(users);

        // Act
        User result = manager.getUserFromMatr("A123456");

        // Assert
        assertNotNull(result);
        assertEquals("A123456", result.Matricule());
        assertEquals("Smith", result.Nom());
        assertEquals("John", result.Prenom());
        assertEquals("john.smith@example.com", result.Email());
    }

    @Test
    void shouldReturnDefaultUserWhenMatriculeDoesNotExist() {
        // Arrange
        List<User> users = List.of(
                new User("A123456", "Smith", "John", "john.smith@example.com"),
                new User("B789101", "Doe", "Jane", "jane.doe@example.com")
        );
        UserManager manager = new UserManager(users);

        // Act
        User result = manager.getUserFromMatr("C123456");

        // Assert
        assertNotNull(result);
        assertEquals("X123456", result.Matricule());
        assertEquals("Doe", result.Nom());
        assertEquals("John", result.Prenom());
        assertEquals("j.doe@helmo.be", result.Email());
    }

    @Test
    void shouldNotThrowExceptionWhenMatriculeExists() {
        // Arrange
        List<User> users = List.of(
                new User("A123456", "Smith", "John", "john.smith@example.com"),
                new User("B789101", "Doe", "Jane", "jane.doe@example.com")
        );
        UserManager manager = new UserManager(users);

        // Act & Assert
        assertDoesNotThrow(() -> manager.existsFromMatr("A123456"));
    }

    @Test
    void shouldThrowExceptionWhenMatriculeDoesNotExist() {
        // Arrange
        List<User> users = List.of(
                new User("A123456", "Smith", "John", "john.smith@example.com"),
                new User("B789101", "Doe", "Jane", "jane.doe@example.com")
        );
        UserManager manager = new UserManager(users);

        // Act & Assert
        UserException exception = assertThrows(UserException.class, () -> manager.existsFromMatr("C123456"));
        assertEquals("Utilisateur non trouvé", exception.getMessage());
    }

    @Test
    void shouldHandleEmptyUserListGracefully() {
        // Arrange
        UserManager manager = new UserManager(List.of());

        // Act
        User result = manager.getUserFromMatr("A123456");

        // Assert
        assertNotNull(result);
        assertEquals("X123456", result.Matricule());
        assertEquals("Doe", result.Nom());
        assertEquals("John", result.Prenom());
        assertEquals("j.doe@helmo.be", result.Email());
    }

    @Test
    void shouldThrowExceptionWhenUserListIsEmpty() {
        // Arrange
        UserManager manager = new UserManager(List.of());

        // Act & Assert
        UserException exception = assertThrows(UserException.class, () -> manager.existsFromMatr("A123456"));
        assertEquals("Utilisateur non trouvé", exception.getMessage());
    }
}