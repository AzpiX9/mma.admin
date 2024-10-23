package org.helmo.mma.admin.domains.rooms;

import org.helmo.mma.admin.domains.core.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CanReadRoomsTest {

    @Mock
    private CanReadRooms canReadRooms;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialisation des mocks
    }

    @Test
    void should_returnListOfRooms_when_GetRooms_IsCalled() {

        Room room1 = new Room("101", "Conference Room", 20);
        Room room2 = new Room("102", "Meeting Room", 10);
        List<Room> mockRooms = Arrays.asList(room1, room2);

        when(canReadRooms.getRooms()).thenReturn(mockRooms);

        List<Room> rooms = canReadRooms.getRooms();

        assertEquals(2, rooms.size());
        assertEquals("Conference Room", rooms.get(0).Name());
        assertEquals(20, rooms.get(0).Size());
        assertEquals("Meeting Room", rooms.get(1).Name());

        verify(canReadRooms, times(1)).getRooms();
    }

    @Test
    void should_ReturnRoom_when_GetRoomById_IsCalled() {
        Room mockRoom = new Room("101", "Conference Room", 20);

        when(canReadRooms.getRoom("101")).thenReturn(mockRoom);
        Room room = canReadRooms.getRoom("101");

        assertNotNull(room);
        assertEquals("101", room.Id());
        assertEquals("Conference Room", room.Name());
        assertEquals(20, room.Size());

        verify(canReadRooms, times(1)).getRoom("101");
    }
}
