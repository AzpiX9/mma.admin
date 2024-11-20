package org.helmo.mma.admin.infrastructures;

import org.helmo.mma.admin.domains.booking.CalendarRepository;
import org.helmo.mma.admin.domains.core.BaseAggregator;
import org.helmo.mma.admin.domains.rooms.CanReadRooms;
import org.helmo.mma.admin.domains.users.CanReadUsers;

import java.util.Objects;

public class BookingAggregator implements BaseAggregator {


    private CalendarRepository calendarRepository;
    private CanReadUsers usersRepo ;
    private CanReadRooms roomsRepo ;

    //TODO : À déplacer
    public BookingAggregator(CanReadRooms roomRepo, CanReadUsers userRepo, CalendarRepository calRepo) {
        this.calendarRepository = Objects.requireNonNull(calRepo) ;
        this.usersRepo = Objects.requireNonNull(userRepo);
        this.roomsRepo = Objects.requireNonNull(roomRepo);
    }

    public CalendarRepository getCalendarRepository() {
        return calendarRepository;
    }

    public CanReadRooms getRoomsRepo() {
        return roomsRepo;
    }

    public CanReadUsers getUsersRepo() {
        return usersRepo;
    }

}
