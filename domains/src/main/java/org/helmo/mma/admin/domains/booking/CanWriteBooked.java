package org.helmo.mma.admin.domains.booking;

import org.helmo.mma.admin.domains.core.Booking;
import org.helmo.mma.admin.domains.core.User;

public interface CanWriteBooked {

    void writeTo(Booking booking, User user);
}
