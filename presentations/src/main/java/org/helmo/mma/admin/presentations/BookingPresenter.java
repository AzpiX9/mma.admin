package org.helmo.mma.admin.presentations;

import org.helmo.mma.admin.domains.core.LocalEvent;

import java.time.LocalDate;
import java.util.List;

public interface BookingPresenter {

    void seeBookingRequest(LocalDate date);

    void writeEventRequest(String request);

    void viewRequest(String request);

    void availableRequest(String request);

    List<String> transform(List<LocalEvent> evs);
}
