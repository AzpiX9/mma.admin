package org.helmo.mma.admin.domains.core;

import org.helmo.mma.admin.domains.exceptions.RoomException;

public record Room(String Id, String Name, int Size) {
    public Room {
        if(Size < 0) {
            throw new RoomException("Capacité invalide");
        }
    }
}
