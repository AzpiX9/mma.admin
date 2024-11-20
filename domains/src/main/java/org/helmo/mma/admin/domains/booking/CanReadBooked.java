package org.helmo.mma.admin.domains.booking;

import org.helmo.mma.admin.domains.core.LocalEvent;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CanReadBooked {
    void readTo();

    List<LocalEvent> retrieveAll(); //TODO: ajouter une date

    LocalEvent getBooking(String idRoom, LocalDateTime slotDT);

    List<LocalEvent> getBookingsBy(String location, LocalDate date);
}
