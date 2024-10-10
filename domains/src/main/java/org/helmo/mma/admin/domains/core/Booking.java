package org.helmo.mma.admin.domains.core;

import java.time.LocalDate;
import java.time.LocalTime;

public record Booking(String IdSalle, String Matricule, LocalDate JourReservation,
                      LocalTime Debut, LocalTime Fin, String Description, int NbPersonnes)  {
}
