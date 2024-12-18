package org.helmo.mma.admin.domains.exceptions;

public class PastDateException extends RuntimeException {
    public PastDateException(String message) {
        super(message);
    }
}
