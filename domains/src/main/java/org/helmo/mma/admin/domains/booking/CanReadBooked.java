package org.helmo.mma.admin.domains.booking;

import org.helmo.mma.admin.domains.core.LocalEvent;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CanReadBooked {
    void readTo();

    List<LocalEvent> retrieveAll();

    LocalEvent getBooking(String id, LocalTime time);

    List<LocalEvent> getBookingsBy(String location, LocalDate date);
}
