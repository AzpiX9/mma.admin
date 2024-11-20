package org.helmo.mma.admin.domains.core;

import org.helmo.mma.admin.domains.booking.CalendarRepository;
import org.helmo.mma.admin.domains.rooms.CanReadRooms;
import org.helmo.mma.admin.domains.users.CanReadUsers;

public interface BaseAggregator {

    CalendarRepository getCalendarRepository() ;

    CanReadRooms getRoomsRepo() ;

    CanReadUsers getUsersRepo() ;
}
