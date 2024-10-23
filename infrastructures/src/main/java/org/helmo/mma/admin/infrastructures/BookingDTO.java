package org.helmo.mma.admin.infrastructures;

import org.helmo.mma.admin.domains.core.Booking;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class BookingDTO {

    private String matricule;
    private String salle;
    private ZonedDateTime debut;
    private ZonedDateTime fin;
    private String description;

    public BookingDTO(Booking booking) {

        this.matricule = booking.Matricule();
        this.salle = booking.IdSalle();
        this.debut = ZonedDateTime.of( booking.JourReservation(),booking.Debut(),ZoneId.systemDefault());
        this.fin = ZonedDateTime.of(booking.JourReservation(),booking.Fin(),ZoneId.systemDefault());
        this.description = booking.Description();
    }

    public String getDescription() {
        return description;
    }

    public String getMatricule() {
        return matricule;
    }

    public String getSalle() {
        return salle;
    }

    public ZonedDateTime getDebut() {
        return debut;
    }

    public ZonedDateTime getFin() {
        return fin;
    }

}
