package org.helmo.mma.admin.presentations;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface MainView {
    void seeReservationsFor(LocalDate dateGiven);

    void bookLocal(String idLocal, String matricule, LocalDateTime localDate );

}
