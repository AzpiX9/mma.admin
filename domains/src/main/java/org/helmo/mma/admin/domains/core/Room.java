package org.helmo.mma.admin.domains.core;

import org.helmo.mma.admin.domains.exceptions.RoomException;

import java.util.Objects;

public final class Room {
    private final String idRoom;
    private final String name;
    private final int capacity;

    public Room(String idRoom, String name, int capacity) throws RoomException {
        if (capacity < 0) {
            throw new RoomException("Capacité invalide");
        }
        this.idRoom = idRoom;
        this.name = name;
        this.capacity = capacity;
    }

    public String idRoom() {
        return idRoom;
    }

    public String name() {
        return name;
    }

    public int capacity() {
        return capacity;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this){
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (Room) obj;
        return Objects.equals(this.idRoom, that.idRoom) &&
                Objects.equals(this.name, that.name) &&
                this.capacity == that.capacity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idRoom, name, capacity);
    }

    public boolean isOversize(int sizeGiven){
        return capacity < sizeGiven;
    }

    @Override
    public String toString() {
        return "Room[" +
                "Id=" + idRoom + ", " +
                "Name=" + name + ", " +
                "Size=" + capacity + ']';
    }

}
