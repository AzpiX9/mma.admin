package org.helmo.mma.admin.domains.services;

import org.helmo.mma.admin.domains.core.Booking;
import org.helmo.mma.admin.domains.core.LocalEvent;
import org.helmo.mma.admin.domains.core.User;

import java.util.List;
import java.util.Map;

public interface BaseServices {

    Map<String, LocalEvent> getCalendar();

    List<String> getServicesFromBooked(String idBooked);

    void addReservationAndServices(Booking booking, User user, List<String> servicesIDs);

    List<String> getServices();
}
