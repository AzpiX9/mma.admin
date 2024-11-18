/*
 * This source file is an example
 */
package org.helmo.mma.admin.presentations;

import org.helmo.mma.admin.domains.booking.AvailableSlotService;
import org.helmo.mma.admin.domains.booking.CalendarRepository;
import org.helmo.mma.admin.domains.core.Booking;
import org.helmo.mma.admin.domains.core.BookingAggregator;
import org.helmo.mma.admin.domains.core.LocalEvent;
import org.helmo.mma.admin.domains.rooms.CanReadRooms;
import org.helmo.mma.admin.domains.users.CanReadUsers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MainPresenter implements BookingPresenter {

    private final MainView view;
    private final CanReadRooms roomsRepo;
    private final CanReadUsers usersRepo;
    private final CalendarRepository calendarRepository;

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
    public void writeEventRequest(String request) {
        var allValues = request.split(", ");
        var formatter = DateTimeFormatter.ofPattern("H:mm");
        Booking booking = new Booking(allValues[0], allValues[1], LocalDate.parse(allValues[2]), LocalTime.parse(allValues[3],formatter), LocalTime.parse(allValues[4],formatter), allValues[5], Integer.parseInt(allValues[6]));

        if (checkIfNotValid(booking)) {
            return;
        }

        var bookerUser = usersRepo.getUser(booking.Matricule());
        calendarRepository.writeTo(booking,bookerUser);
        view.displayMessage("Évenement crée avec succès");
    }

    private boolean checkIfNotValid(Booking booking) {
        var collision = calendarRepository.getBookingsBy(booking.IdSalle(), booking.JourReservation())
                .stream().anyMatch(ev -> ev.Debut().isBefore(booking.Fin()) && booking.Debut().isBefore(ev.Fin()) );
        if(collision){
            view.displayError("Crénau occupé");
            return true;
        }
        if(booking.NbPersonnes() > roomsRepo.getRoom(booking.IdSalle()).Size()){
            view.displayError("Capacité insuffisante");
            return true;
        }
        if(!usersRepo.exists(booking.Matricule())){
            view.displayError("Utilisateur non trouvé");
            return true;
        }


        return false;
    }

    @Override
    public void viewRequest(String request) {
        var values = request.split(", ");
        var givenDT = LocalDateTime.of(LocalDate.parse(values[0]),LocalTime.parse(values[2]));
        var bookedFound = calendarRepository.getBooking(values[1], givenDT);

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

    @Override
    public void availableRequest(String request) {
        var values = request.split(", ");
        var durationGiven = LocalTime.parse(values[2]);
        List<String> evs = new ArrayList<>();

        var roomResized = roomsRepo.getRooms().stream().filter(r -> Integer.parseInt(values[1]) <= r.Size()).toList();
        for (int plusDay = 0; plusDay <= 2; plusDay++) {
            for(var room : roomResized){
                var slotsAvailable = new AvailableSlotService(calendarRepository.getBookingsBy(room.Id(),LocalDate.parse(values[0]).plusDays(plusDay)));
                if(durationGiven.isBefore(slotsAvailable.getTimeDifference())){
                    var slot = slotsAvailable.computeTimeSlot().split("-");
                    var singleValue = String.format("%-5s | %s | %-14s | %-12s |",room.Id(),LocalDate.parse(values[0]).plusDays(plusDay),slot[0],slot[1]);
                    evs.add(singleValue);
                }
            }
        }

        view.displayAvailable(evs);
    }


}
