package org.helmo.mma.admin.domains.core;

import org.helmo.mma.admin.domains.booking.EventUtils;
import org.helmo.mma.admin.domains.exceptions.BookingException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class BookingManager {

    private static final LocalTime START_TIME = LocalTime.of(8, 0);
    private static final LocalTime END_TIME = LocalTime.of(17, 0);
    private static final int MAX_DAY_SEARCH = 2;

    private List<LocalEvent> bookings;

    public BookingManager(List<LocalEvent> bookings) {
        this.bookings = bookings;
    }

    public LocalEvent getBooking(String idRoom, LocalDateTime givenTime) {
        LocalEvent result = null;

        for(var event : bookings) {
            if(event.Location().equals(idRoom) && isBetweenTime(event,givenTime)) {
                result = event;
            }
        }
        return result;
    }

    public List<LocalEvent> getBookingsBy(String location, LocalDate date){
        return bookings
                .stream()
                .filter(s -> s.Location().equals(location) && date.equals(s.DateJour()))
                .toList();
    }

    private boolean isBetweenTime(LocalEvent event, LocalDateTime crenau) {

        var eventReferenceStart = LocalDateTime.of(event.DateJour(),event.Debut());
        var eventReferenceEnd = LocalDateTime.of(event.DateJour(),event.Fin());
        return (crenau.equals(eventReferenceStart))
                || (crenau.isAfter(eventReferenceStart)
                && crenau.isBefore(eventReferenceEnd));
    }

    public void checkIfNotValid(Booking booking, Room room) {
        var collision = getBookingsBy(booking.IdSalle(), booking.JourReservation())
                .stream().anyMatch(ev -> ev.Debut().isBefore(booking.Fin()) && booking.Debut().isBefore(ev.Fin()) );
        if(collision){
            throw new BookingException("Crénau occupé");
        }
        if(booking.NbPersonnes() > room.Size()){
            throw new BookingException("Capacité insuffisante");
        }

    }

    public List<String> eventsToString(LocalDate dateGiven, String roomId){
        return EventUtils.transform(getBookingsBy(roomId,dateGiven));
    }

    public List<String> checkAvailableOnAll(List<Room> roomAll, LocalDate dateGiven, String duree){
        List<String> results = new ArrayList<>();
        for (int i = 0; i <= MAX_DAY_SEARCH; i++) {
            for(var room: roomAll) {
                var r = getAvailableSlotsFromDuration(room.Id(), dateGiven.plusDays(i), duree);
                int index = i;
                results.addAll(r.stream().map(a -> room.Id()+","+dateGiven.plusDays(index)+","+a).toList());
            }
        }
        return results.subList(0, Math.min(5, results.size()));
    }

    private List<String> getAvailableSlotsFromDuration(String roomId,LocalDate dateGiven, String duree) {
        var dureeMinimaleMinutes = convertirEnMinutes(duree);

        var byDay = bookings.stream().filter(b -> b.DateJour().equals(dateGiven) && b.Location().equals(roomId)).toList();


        List<String> momentsLibres = new ArrayList<>();
        final String[] dernierFin = {START_TIME.toString()};

        byDay.forEach(e -> {
            if (!e.Debut().toString().equals(dernierFin[0])) {
                momentsLibres.add(dernierFin[0] +"-"+ e.Debut());
            }
            dernierFin[0] = e.Fin().toString();
        });

        if (!dernierFin[0].equals(END_TIME.toString())) {
            momentsLibres.add(dernierFin[0] +"-"+ END_TIME);
        }

        return momentsLibres.stream()
                .filter(libre ->
                    calculerDuree(libre.split("-")[0], libre.split("-")[1]) >= dureeMinimaleMinutes
                )
                .toList();
    }

    // Convertir une durée au format HH:mm en minutes
    private static int convertirEnMinutes(String duree) {
        String[] parts = duree.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    // Calculer la durée entre deux horaires au format HH:mm en minutes
    private static int calculerDuree(String debut, String fin) {
        int debutMinutes = convertirEnMinutes(debut);
        int finMinutes = convertirEnMinutes(fin);
        return finMinutes - debutMinutes;
    }

}
