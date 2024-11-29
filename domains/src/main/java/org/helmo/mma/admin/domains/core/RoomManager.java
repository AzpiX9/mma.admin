package org.helmo.mma.admin.domains.core;

import java.util.ArrayList;
import java.util.List;

public class RoomManager {

    private List<Room> rooms = new ArrayList<>();

    public RoomManager(List<Room> rooms) {
        this.rooms = rooms;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public Room getARoom(String idRoom) {
        return rooms.stream().filter(r -> r.Id().equals(idRoom)).findFirst().orElse(null);
    }

    public List<Room> getRoomsByMaxSize(int maxSize) {
        return rooms.stream().filter(r -> maxSize <= r.Size()).toList();
    }
}
