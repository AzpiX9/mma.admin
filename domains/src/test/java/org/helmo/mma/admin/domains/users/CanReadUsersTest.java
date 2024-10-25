package org.helmo.mma.admin.domains.users;

import org.helmo.mma.admin.domains.core.User;
import org.helmo.mma.admin.domains.users.CanReadUsers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CanReadUsersTest {

    @Mock
    private CanReadUsers canReadUsers;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialisation des mocks
    }

    @Test
    void should_returnListOfUsers_when_GetUsers_IsCalled() {

        User user1 = new User("123", "Doe", "John", "john.doe@example.com");
        User user2 = new User("456", "Smith", "Jane", "jane.smith@example.com");
        List<User> mockUsers = Arrays.asList(user1, user2);

        when(canReadUsers.getUsers()).thenReturn(mockUsers);

        List<User> users = canReadUsers.getUsers();

        assertEquals(2, users.size());
        assertEquals("Doe", users.get(0).Nom()); // Nom est un getter auto-généré
        assertEquals("John", users.get(0).Prenom());
        assertEquals("jane.smith@example.com", users.get(1).Email());

        verify(canReadUsers, times(1)).getUsers();
    }

    @Test
    void should_ReturnUser_when_GetUserById_IsCalled() {
        User mockUser = new User("123", "Doe", "John", "john.doe@example.com");

        when(canReadUsers.getUser("123")).thenReturn(mockUser);

        User user = canReadUsers.getUser("123");

        assertNotNull(user);
        assertEquals("123", user.Matricule());
        assertEquals("Doe", user.Nom());
        assertEquals("John", user.Prenom());
        assertEquals("john.doe@example.com", user.Email());

        verify(canReadUsers, times(1)).getUser("123");
    }

    @Test
    void should_ReturnBoolean_when_Exists_IsCalled() {
        when(canReadUsers.exists("123")).thenReturn(true);

        assertTrue(canReadUsers.exists("123"));

        verify(canReadUsers, times(1)).exists("123");

        when(canReadUsers.exists("999")).thenReturn(false);
        assertFalse(canReadUsers.exists("999"));

        verify(canReadUsers, times(1)).exists("999");
    }
}
