package org.helmo.mma.admin.domains.exceptions;

public class BookingException extends RuntimeException {
    public BookingException(String message) {
        super("BookingException -> "+message);
    }
}
