package org.helmo.mma.admin.domains.exceptions;

public class DirException extends Exception {
    public DirException(String message) {
        super("DirException ->"+message);
    }
}
