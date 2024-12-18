package org.helmo.mma.admin.infrastructures;

import org.helmo.mma.admin.domains.core.Room;
import org.helmo.mma.admin.domains.exceptions.RoomException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoomDbRepositoryTest {
    private static final String GIVEN_PATH = "jdbc:sqlite:src/test/resources/dbTests.sqlite";
    private Connection connection;
    private RoomDbRepository repository;

    @BeforeEach
    public void setUp() {
        try {
            connection = DriverManager.getConnection(GIVEN_PATH);
            for (var query : DbTestUtils.insertAllRooms()){
                connection.createStatement().executeUpdate(query);
            }
            repository = new RoomDbRepository(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    public void tearDown() {
        try {
            connection.createStatement().executeUpdate("DROP TABLE IF EXISTS Rooms;");
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void shouldReturnAllRooms() throws RoomException {
        var allRooms = repository.getRooms();
        var expected = List.of(
                new Room("LC1","Learning Center 1",20),
                new Room("LC2","Learning Center 2",20),
                new Room("LB1","Learning Box 1",5),
                new Room("LB2","Learning Box 2",5)
        );
        assertEquals(expected, allRooms);
    }

    @Test
    public void shouldReturnRoomById() throws RoomException {
        var idRoom = "LC1";
        var anyRoom = repository.getRoom(idRoom);

        var expected = new Room("LC1", "Learning Center 1", 20);
        assertEquals(expected, anyRoom);
        assertEquals("LC1",anyRoom.idRoom());
        assertEquals("Learning Center 1",anyRoom.name());
        assertEquals(20,anyRoom.capacity());
    }
    @Test
    public void shouldThrowWhenByInvalidId() {
        var idRoom = "LC3";

        var exception = assertThrows(RoomException.class, () -> repository.getRoom(idRoom));
        assertEquals("Room not found", exception.getMessage());

    }

}