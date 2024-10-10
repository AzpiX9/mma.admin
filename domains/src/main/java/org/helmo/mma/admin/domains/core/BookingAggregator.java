package org.helmo.mma.admin.domains.core;

import org.helmo.mma.admin.domains.booking.CalendarRepository;
import org.helmo.mma.admin.domains.rooms.CanReadRooms;
import org.helmo.mma.admin.domains.users.CanReadUsers;

import java.util.Objects;

public class BookingAggregator {


    private CalendarRepository calendarRepository;
    private CanReadUsers usersRepo ;
    private CanReadRooms roomsRepo ;

    public BookingAggregator(CanReadRooms roomRepo, CanReadUsers userRepo, CalendarRepository calRepo) {
        this.calendarRepository = Objects.requireNonNull(calRepo) ;
        this.usersRepo = Objects.requireNonNull(userRepo);
        this.roomsRepo = Objects.requireNonNull(roomRepo);
    }


}
