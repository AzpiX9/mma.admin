package org.helmo.mma.admin.domains.core;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record LocalEvent(String Username, String Location, LocalDate DateJour, LocalTime Debut, LocalTime Fin, String Summary) {
    public boolean isOccupied(LocalDateTime timeGiven){
        return LocalDateTime.of(DateJour,Debut).equals(timeGiven)
                || timeGiven.isAfter(LocalDateTime.of(DateJour,Debut))
                && timeGiven.isBefore(LocalDateTime.of(DateJour,Fin));
    }

    public boolean isBeforeBegin(LocalTime timeGiven){
        return Debut.isBefore(timeGiven);
    }
}
