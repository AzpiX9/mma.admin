package org.helmo.mma.admin.domains.core;

import org.helmo.mma.admin.domains.exceptions.BookingException;

import java.time.LocalDate;
import java.time.LocalTime;

public record Booking(String IdSalle, String Matricule, LocalDate JourReservation,
                      LocalTime Debut, LocalTime Fin, String Description, int NbPersonnes)  {
    public Booking {
        if(NbPersonnes < 0){
            throw new BookingException("Nombre de personne ne peut pas être négatif");
        }
    }
}
