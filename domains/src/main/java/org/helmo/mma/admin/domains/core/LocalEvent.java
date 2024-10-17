package org.helmo.mma.admin.domains.core;

import java.time.LocalDate;
import java.time.LocalTime;

public record LocalEvent(String Username, String Location, LocalDate DateJour, LocalTime Debut, LocalTime Fin, String Summary) {
}
