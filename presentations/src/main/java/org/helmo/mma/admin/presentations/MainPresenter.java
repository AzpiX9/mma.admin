/*
 * This source file is an example
 */
package org.helmo.mma.admin.presentations;

import org.helmo.mma.admin.domains.booking.CalendarRepository;
import org.helmo.mma.admin.domains.core.Booking;
import org.helmo.mma.admin.domains.core.BookingAggregator;
import org.helmo.mma.admin.domains.rooms.CanReadRooms;
import org.helmo.mma.admin.domains.users.CanReadUsers;

import java.time.LocalDate;
import java.time.LocalTime;

public class MainPresenter implements BookingPresenter {

    private MainView view;
    private CanReadRooms roomsRepo;
    private CanReadUsers usersRepo;
    private CalendarRepository calendarRepository;

    public MainPresenter(BookingAggregator aggregator) {
        this.roomsRepo = aggregator.getRoomsRepo();
        this.usersRepo = aggregator.getUsersRepo();
        this.calendarRepository = aggregator.getCalendarRepository();
    }

    @Override
    public void setView(MainView view) {
        this.view = view;
        view.seeReservationsFor(LocalDate.now());
    }

    @Override
    public void seeBookingRequest(LocalDate date) {
        for (var room : roomsRepo.getRooms()) {
            System.out.println(room.Id());
        }
    }

    @Override
    public void handleRequest(String request) {
        var allValues = request.split(", ");

        Booking booking = new Booking(
                allValues[0], allValues[1], LocalDate.parse(allValues[2]), LocalTime.parse(allValues[3]), LocalTime.parse(allValues[4]), allValues[5], Integer.parseInt(allValues[6])
        );

        //Si toutes les conditons sont valides alors on peut enregistrer la réservation
        if(!usersRepo.exists(booking.Matricule())){ //TODO: vérifier si une room existe aussi depuis un fichier ou db
            view.displayError("Objet non trouvé");
            return;
        }
        if(booking.NbPersonnes() > roomsRepo.getRoom(booking.IdSalle()).Size()){
            view.displayError("Capacité insuffisante");
            return;
        }
        var bookerUser = usersRepo.getUser(booking.Matricule());
        calendarRepository.writeTo(booking,bookerUser);
        view.seeReservationsFor(LocalDate.now());
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
                room.Name()+"_"+room.Size()
                +","+bookedFound.DateJour()+","+bookedFound.Debut()
                +","+bookedFound.Fin()+","+userString+","+bookedFound.Summary()
        );
    }


}
