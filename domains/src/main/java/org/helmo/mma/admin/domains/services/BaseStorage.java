package org.helmo.mma.admin.domains.services;

import org.helmo.mma.admin.domains.booking.CalendarRepository;

public interface BaseStorage {

    SevicesRepository getSevicesRepository();
    CalendarRepository getCalendarRepository();

    void beginTransaction();
    void rollbackTransaction();
    void commitTransaction();
}
