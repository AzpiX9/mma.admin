package org.helmo.mma.admin.presentations;

import java.time.LocalDate;

public interface MainView {
    void seeReservationsFor(LocalDate dateGiven);

    void displayError(String error);

    void displayAReservation(String reservation);

}
