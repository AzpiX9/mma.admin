package org.helmo.example.presentations;

import java.time.LocalDateTime;

public interface MainView {
    void seeReservationsFor(LocalDateTime dateTimeGiven);

    void bookLocal(String idLocal, String matricule, LocalDateTime localDate );

}
