package org.helmo.mma.admin.domains.core;

import org.helmo.mma.admin.domains.booking.CalendarRepository;
import org.helmo.mma.admin.domains.booking.EventUtils;
import org.helmo.mma.admin.domains.rooms.CanReadRooms;
import org.helmo.mma.admin.domains.users.CanReadUsers;

import java.time.LocalDate;
import java.util.List;
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

    public CalendarRepository getCalendarRepository() {
        return calendarRepository;
    }

    public CanReadRooms getRoomsRepo() {
        return roomsRepo;
    }

    public CanReadUsers getUsersRepo() {
        return usersRepo;
    }

    public String checkIfNotValid(Booking booking) {
        var collision = calendarRepository.getBookingsBy(booking.IdSalle(), booking.JourReservation())
                .stream().anyMatch(ev -> ev.Debut().isBefore(booking.Fin()) && booking.Debut().isBefore(ev.Fin()) );
        if(collision){
            return "Crénau occupé";
        }
        if(booking.NbPersonnes() > roomsRepo.getRoom(booking.IdSalle()).Size()){
            return "Capacité insuffisante";
        }
        if(!usersRepo.exists(booking.Matricule())){
            return "Utilisateur non trouvé";
        }

        return "";
    }

    public User getUserFromMatricule(String matricule) {
        return usersRepo.getUser(matricule);
    }

    public List<Room> getRooms(){
        return roomsRepo.getRooms();
    }

    public List<String> eventsToString(LocalDate dateGiven, String roomId){
        return EventUtils.transform(calendarRepository.getBookingsBy(roomId,dateGiven));
    }
}
