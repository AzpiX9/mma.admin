package org.helmo.mma.admin.presentations;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface MainView {
    void setPresenter(BookingPresenter presenter);

    void seeReservationsFor(LocalDate dateGiven);

    void displayError(String error);

    void displayAReservation(String reservation,List<String>servicesChosen);

    void displayByLocalEvs(String name, List<String> elems);

    Set<String> askService(List<String> availableServices);

    void displayAvailable(List<String> evs);

    void displayMessage(String message);
}
