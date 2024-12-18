package org.helmo.mma.admin.domains.rooms;

import org.helmo.mma.admin.domains.core.Room;
import org.helmo.mma.admin.domains.exceptions.RoomException;

import java.util.List;


public interface CanReadRooms {
    List<Room> getRooms();
    Room getRoom(String roomId) throws RoomException; //TODO: déplacer dans un objet métier
}
