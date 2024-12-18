/*
 * This source file is an example
 */
package org.helmo.mma.admin.presentations;

import org.helmo.mma.admin.domains.core.*;
import org.helmo.mma.admin.domains.exceptions.BookingException;
import org.helmo.mma.admin.domains.exceptions.EventNotFoundException;
import org.helmo.mma.admin.domains.exceptions.PastDateException;
import org.helmo.mma.admin.domains.exceptions.UserException;
import org.helmo.mma.admin.domains.services.BaseServices;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MainPresenter implements BookingPresenter {

    private final MainView view;
    private final BaseServices reservationService;
    private final UserManager usersAll;
    private final RoomManager roomsAll;
    private final BookingManager eventsAll;

    public MainPresenter(MainView view , BaseAggregator aggregator) throws UserException {
        this.usersAll = new UserManager(aggregator.getUsersRepo().getUsers());
        this.roomsAll = new RoomManager(aggregator.getRoomsRepo().getRooms());
        this.eventsAll = new BookingManager(aggregator.getCalendarRepository().retrieveAll());
        this.reservationService = aggregator.getAService();
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void seeBookingRequest(LocalDate date) {
        for (var room : roomsAll.getRooms()) {
            var eventByRooms = transform(eventsAll.getBookingsBy(room.idRoom(),date));
            view.displayByLocalEvs(room.idRoom(), eventByRooms);
        }
    }

    private static List<String> transform(List<LocalEvent> evs){
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

        var services = view.askService(reservationService.getServices());
        try {
            var tempRoom = roomsAll.getARoom(allValues[0]);
            eventsAll.checkIfNotValid(booking,tempRoom);
            var bookerUser = usersAll.getUserFromMatr(booking.Matricule());
            usersAll.existsFromMatr(bookerUser.Matricule());
            reservationService.addReservationAndServices(booking,bookerUser,new ArrayList<>(services));
            eventsAll.replaceAll(reservationService.getCalendar());
            view.displayMessage("Évenement crée avec succès");
        } catch (BookingException | UserException | PastDateException e) {
            view.displayError(e.getMessage());
        }
    }

    @Override
    public void viewRequest(String request) {
        var values = request.split(", ");
        var givenDT = LocalDateTime.of(LocalDate.parse(values[0]),LocalTime.parse(values[2]));

        try{
            var bookedFound = eventsAll.getBooking(values[1], givenDT);
            var room = roomsAll.getARoom(bookedFound.Location());
            var user = usersAll.getUserFromMatr(bookedFound.Username());
            var userString = String.format("%s_%s_%s_%s",user.Prenom(),user.Nom(),user.Matricule(),user.Email());
            var servicesChoosen = reservationService.getServicesFromBooked(eventsAll.getIdFromReservation(bookedFound));

            view.displayAReservation(
                    room.name()+"_"+room.capacity() +","+bookedFound.DateJour()+","+bookedFound.Debut()
                            +","+bookedFound.Fin()+","+userString+","+bookedFound.Summary()
            ,servicesChoosen);
        }catch (EventNotFoundException | BookingException e){
            view.displayError(e.getMessage());
        }
    }

    @Override
    public void availableRequest(String request) {
        var values = request.split(", ");
        var roomResized = roomsAll.getRoomsByMaxSize(Integer.parseInt(values[1]));
        List<String> evs = eventsAll.checkAvailableOnAll(roomResized,LocalDate.parse(values[0]),values[2]);
        view.displayAvailable(evs);
    }

}
