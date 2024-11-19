package org.helmo.mma.admin.infrastructures;

import org.helmo.mma.admin.domains.core.Booking;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public record BookingDTO(String Matricule, String Salle, ZonedDateTime Debut, ZonedDateTime Fin, String Description) {

    public BookingDTO(Booking booking) {
        this(booking.Matricule(),
                booking.IdSalle(),
                ZonedDateTime.of(booking.JourReservation(), booking.Debut(), ZoneId.systemDefault()),
                ZonedDateTime.of(booking.JourReservation(), booking.Fin(), ZoneId.systemDefault()),
                booking.Description());
    }
}
