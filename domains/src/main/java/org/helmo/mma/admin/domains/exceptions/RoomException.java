package org.helmo.mma.admin.domains.exceptions;

public class RoomException extends RuntimeException {
    public RoomException(String message) {
        super("RoomException "+message);
    }
}
