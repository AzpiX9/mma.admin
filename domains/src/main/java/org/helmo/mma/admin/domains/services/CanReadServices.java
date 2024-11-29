package org.helmo.mma.admin.domains.services;

import java.util.List;

public interface CanReadServices {
    List<String> retrieveAvailableServices();

    List<String> retriveServicesFromBooking(String bookingId);
}
