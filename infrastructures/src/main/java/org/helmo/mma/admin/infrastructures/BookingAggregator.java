package org.helmo.mma.admin.infrastructures;

import org.helmo.mma.admin.domains.booking.CalendarRepository;
import org.helmo.mma.admin.domains.core.BaseAggregator;
import org.helmo.mma.admin.domains.rooms.CanReadRooms;
import org.helmo.mma.admin.domains.services.BaseServices;
import org.helmo.mma.admin.domains.users.CanReadUsers;

import java.util.Objects;

public class BookingAggregator implements BaseAggregator {


    private CalendarRepository calendarRepository;
    private CanReadUsers usersRepo ;
    private CanReadRooms roomsRepo ;
    private BaseServices aService;

    public BookingAggregator(CanReadRooms roomRepo, CanReadUsers userRepo, CalendarRepository calRepo, BaseServices aService) {
        this.calendarRepository = Objects.requireNonNull(calRepo) ;
        this.usersRepo = Objects.requireNonNull(userRepo);
        this.roomsRepo = Objects.requireNonNull(roomRepo);
        this.aService = Objects.requireNonNull(aService);

    }

    @Override
    public CalendarRepository getCalendarRepository() {
        return calendarRepository;
    }

    @Override
    public CanReadRooms getRoomsRepo() {
        return roomsRepo;
    }

    @Override
    public CanReadUsers getUsersRepo() {
        return usersRepo;
    }

    @Override
    public BaseServices getAService() {
        return aService;
    }

}
