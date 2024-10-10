package org.helmo.mma.admin.domains.booking;

import java.util.List;

public interface CanReadBooked <T> {
    void readTo();

    List<T> retrieveAll();
}
