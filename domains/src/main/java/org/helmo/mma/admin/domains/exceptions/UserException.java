package org.helmo.mma.admin.domains.exceptions;

public class UserException extends RuntimeException {
    public UserException(String message) {
        super("UserException "+message);
    }
}
