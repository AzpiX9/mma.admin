package org.helmo.mma.admin.infrastructures;

import org.helmo.mma.admin.domains.core.Booking;

import java.time.ZonedDateTime;

public class BookingDTO {

    private String matricule;
    private String salle;
    private ZonedDateTime debut;
    private ZonedDateTime fin;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String description;

    public BookingDTO(Booking booking) {
        this.matricule = booking.Matricule();
        this.salle = booking.IdSalle();
        this.debut = ZonedDateTime.from(booking.Debut());
        this.fin = ZonedDateTime.from(booking.Fin());
    }

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public String getSalle() {
        return salle;
    }

    public void setSalle(String salle) {
        this.salle = salle;
    }

    public ZonedDateTime getDebut() {
        return debut;
    }

    public void setDebut(ZonedDateTime debut) {
        this.debut = debut;
    }

    public ZonedDateTime getFin() {
        return fin;
    }

    public void setFin(ZonedDateTime fin) {
        this.fin = fin;
    }
}
