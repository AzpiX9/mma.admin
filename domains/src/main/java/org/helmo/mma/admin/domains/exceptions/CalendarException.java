package org.helmo.mma.admin.domains.exceptions;

public class CalendarException extends RuntimeException {
    public CalendarException(String message) {
        super("CalendarException -> "+message);
    }
}
