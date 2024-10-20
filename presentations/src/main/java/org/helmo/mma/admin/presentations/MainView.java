package org.helmo.mma.admin.presentations;

import java.time.LocalDate;
import java.util.List;

public interface MainView {
    void setPresenter(BookingPresenter presenter);

    void seeReservationsFor(LocalDate dateGiven);

    void displayError(String error);

    void displayAReservation(String reservation);

    public void displayByLocalEvs(String name,List<String> elems);

}
