package org.helmo.mma.admin.domains.booking;

import org.helmo.mma.admin.domains.core.Booking;

import java.util.List;

public interface CanReadBooked <T> {
    void readTo();

    List<T> retrieveAll();

    Booking getBooking(String id);
}
