package org.helmo.mma.admin.domains.booking;

import org.helmo.mma.admin.domains.core.Booking;

public interface CanWriteBooked {

    void writeTo(Booking booking);
}
