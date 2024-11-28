package org.helmo.mma.admin.domains.services;

import java.util.List;

public interface CanWriteServices {
    void insertReservation(String bookingId, List<String> reservation);
}
