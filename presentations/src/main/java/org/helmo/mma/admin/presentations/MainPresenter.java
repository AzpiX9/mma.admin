/*
 * This source file is an example
 */
package org.helmo.mma.admin.presentations;

import org.helmo.mma.admin.domains.booking.CalendarRepository;
import org.helmo.mma.admin.domains.core.Booking;
import org.helmo.mma.admin.domains.core.BookingAggregator;
import org.helmo.mma.admin.domains.core.LocalEvent;
import org.helmo.mma.admin.domains.rooms.CanReadRooms;
import org.helmo.mma.admin.domains.users.CanReadUsers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MainPresenter implements BookingPresenter {

    private MainView view;
    private CanReadRooms roomsRepo;
    private CanReadUsers usersRepo;
    private CalendarRepository calendarRepository;

    public MainPresenter(MainView view ,BookingAggregator aggregator) {
        this.roomsRepo = aggregator.getRoomsRepo();
        this.usersRepo = aggregator.getUsersRepo();
        this.calendarRepository = aggregator.getCalendarRepository();
        this.view = view;
        this.view.setPresenter(this);
    }


    @Override
    public void seeBookingRequest(LocalDate date) {
        for (var room : roomsRepo.getRooms()) {
            var eventByRooms = calendarRepository.getBookingsBy(room.Id(),date);

            view.displayByLocalEvs(room.Id(), transform(eventByRooms));
        }
    }

    private List<String> transform(List<LocalEvent> evs){
        List<String> times = new ArrayList<>();
        for (LocalEvent event : evs) {
            var tempStr = event.Debut()+"-"+event.Fin();
            times.add(tempStr);
        }

        return times;
    }

    @Override
    public void handleRequest(String request) {
        var allValues = request.split(", ");
        Booking booking = new Booking(allValues[0], allValues[1], LocalDate.parse(allValues[2]), LocalTime.parse(allValues[3]), LocalTime.parse(allValues[4]), allValues[5], Integer.parseInt(allValues[6]));

        if (checkIfNotValid(booking)) return;

        var bookerUser = usersRepo.getUser(booking.Matricule());
        calendarRepository.writeTo(booking,bookerUser);
    }

    private boolean checkIfNotValid(Booking booking) {
        if(!usersRepo.exists(booking.Matricule())){
            view.displayError("Objet non trouvé");
            return true;
        }
        if(booking.NbPersonnes() > roomsRepo.getRoom(booking.IdSalle()).Size()){
            view.displayError("Capacité insuffisante");
            return true;
        }
        var collision = calendarRepository.getBookingsBy(booking.IdSalle(), booking.JourReservation())
                .stream().anyMatch(ev -> ev.Debut().isBefore(booking.Fin()) && booking.Debut().isBefore(ev.Fin()) );
        if(collision){
            view.displayError("Crénau occupé");
            return true;
        }
        return false;
    }

    @Override
    public void viewRequest(String request) {
        var values = request.split(", ");
        var bookedFound = calendarRepository.getBooking(values[1], LocalTime.parse(values[2]));

        if(bookedFound == null){
            view.displayError("Aucune correspondance trouvée");
            return;
        }
        var room = roomsRepo.getRoom(values[1]);
        var userString = bookedFound.Username();

        view.displayAReservation(
                room.Name()+"_"+room.Size() +","+bookedFound.DateJour()+","+bookedFound.Debut()
                +","+bookedFound.Fin()+","+userString+","+bookedFound.Summary()
        );

    }


}
