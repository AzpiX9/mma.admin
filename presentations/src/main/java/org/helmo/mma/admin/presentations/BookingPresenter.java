package org.helmo.mma.admin.presentations;

import java.time.LocalDate;

public interface BookingPresenter {
    void setView(MainView view);

    void seeBookingRequest(LocalDate date);

    void handleRequest(String request);
}
