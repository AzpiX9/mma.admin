package org.helmo.mma.admin.presentations;

import java.time.LocalDate;

public interface BookingPresenter {

    void seeBookingRequest(LocalDate date);

    void writeEventRequest(String request);

    void viewRequest(String request);

    void availableRequest(String request);
}
