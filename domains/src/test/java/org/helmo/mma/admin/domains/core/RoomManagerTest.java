package org.helmo.mma.admin.domains.core;

import org.helmo.mma.admin.domains.exceptions.RoomException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoomManagerTest {
    @Test
    void shouldReturnAllRooms() throws RoomException {
        // Arrange
        List<Room> rooms = List.of(
                new Room("1", "Conference Room", 10),
                new Room("2", "Meeting Room", 5)
        );
        RoomManager manager = new RoomManager(rooms);

        // Act
        List<Room> result = manager.getRooms();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("1", result.get(0).idRoom());
        assertEquals("2", result.get(1).idRoom());
    }

    @Test
    void shouldReturnRoomById() throws RoomException {
        // Arrange
        List<Room> rooms = List.of(
                new Room("1", "Conference Room", 10),
                new Room("2", "Meeting Room", 5)
        );
        RoomManager manager = new RoomManager(rooms);

        // Act
        Room room = manager.getARoom("1");

        // Assert
        assertNotNull(room);
        assertEquals("1", room.idRoom());
        assertEquals("Conference Room", room.name());
    }

    @Test
    void shouldReturnNullIfRoomIdNotFound() throws RoomException {
        // Arrange
        List<Room> rooms = List.of(
                new Room("1", "Conference Room", 10),
                new Room("2", "Meeting Room", 5)
        );
        RoomManager manager = new RoomManager(rooms);

        // Act
        Room room = manager.getARoom("3");

        // Assert
        assertNull(room);
    }

    @Test
    void shouldReturnRoomsByMaxSize() throws RoomException {
        // Arrange
        List<Room> rooms = List.of(
                new Room("1", "Large Conference Room", 15),
                new Room("2", "Medium Meeting Room", 10),
                new Room("3", "Small Office", 5)
        );
        RoomManager manager = new RoomManager(rooms);

        // Act
        List<Room> result = manager.getRoomsByMaxSize(10);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(r -> r.idRoom().equals("1")));
        assertTrue(result.stream().anyMatch(r -> r.idRoom().equals("2")));
    }

    @Test
    void shouldReturnEmptyListIfNoRoomMatchesMaxSize() throws RoomException {
        // Arrange
        List<Room> rooms = List.of(
                new Room("1", "Small Office", 5)
        );
        RoomManager manager = new RoomManager(rooms);

        // Act
        List<Room> result = manager.getRoomsByMaxSize(10);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldHandleEmptyRoomList() {
        // Arrange
        RoomManager manager = new RoomManager(List.of());

        // Act
        List<Room> allRooms = manager.getRooms();
        Room room = manager.getARoom("1");
        List<Room> filteredRooms = manager.getRoomsByMaxSize(10);

        // Assert
        assertTrue(allRooms.isEmpty());
        assertNull(room);
        assertTrue(filteredRooms.isEmpty());
    }
}