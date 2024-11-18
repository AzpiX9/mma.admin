package org.helmo.mma.admin.infrastructures;

import org.helmo.mma.admin.domains.booking.CalendarRepository;
import org.helmo.mma.admin.domains.core.Booking;
import org.helmo.mma.admin.domains.core.LocalEvent;
import org.helmo.mma.admin.domains.core.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CalDbRepository implements CalendarRepository {
    @Override
    public void readTo() {

    }

    @Override
    public List<LocalEvent> retrieveAll() {
        return List.of();
    }

    @Override
    public LocalEvent getBooking(String id, LocalDateTime slotDT) {
        return null;
    }

    @Override
    public List<LocalEvent> getBookingsBy(String location, LocalDate date) {
        return List.of();
    }

    @Override
    public void writeTo(Booking booking, User user) {

    }
}
