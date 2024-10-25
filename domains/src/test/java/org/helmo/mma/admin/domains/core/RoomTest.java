package org.helmo.mma.admin.domains.core;

import org.helmo.mma.admin.domains.exceptions.RoomException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoomTest {
    @Test
    public void should_return_a_valid_object() {
        Room room = new Room("R123", "Conference Room", 50);

        assertEquals("R123", room.Id());
        assertEquals("Conference Room", room.Name());
        assertEquals(50, room.Size());
    }

    @Test
    public void should_return_true_when_two_objects_are_equal() {
        Room room1 = new Room("R123", "Conference Room", 50);
        Room room2 = new Room("R123", "Conference Room", 50);

        assertEquals(room1, room2);
        assertEquals(room1.hashCode(), room2.hashCode());
    }

    @Test
    public void should_return_a_string_representation_with_its_values() {
        Room room = new Room("R123", "Conference Room", 50);

        String expected = "Room[Id=R123, Name=Conference Room, Size=50]";
        assertEquals(expected, room.toString());
    }

    @Test
    public void testInvalidRoomSize() {
        assertThrows(RoomException.class, () -> {
            new Room("R123", "Conference Room", -10);
        });
    }


}