package org.helmo.mma.admin.infrastructures;

import org.helmo.mma.admin.domains.booking.CalendarRepository;
import org.helmo.mma.admin.domains.core.Booking;
import org.helmo.mma.admin.domains.core.LocalEvent;
import org.helmo.mma.admin.domains.core.User;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CalDbRepository implements CalendarRepository {

    private Connection connection;
    private List<LocalEvent> localEvents;

    public CalDbRepository(Connection connection) {
        this.connection = Objects.requireNonNull(connection);
        readTo();
    }

    @Override
    public void readTo() {
        var localEvents = new ArrayList<LocalEvent>();
        var query = "SELECT * FROM Reservation";

        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                localEvents.add(new LocalEvent(
                        resultSet.getString("salle"),
                        resultSet.getString("matricule"),
                        resultSet.getDate("jourReservation").toLocalDate(),
                        resultSet.getTime("debut").toLocalTime(),
                        resultSet.getTime("fin").toLocalTime(),
                        resultSet.getString("description")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.localEvents = localEvents;
    }

    @Override
    public List<LocalEvent> retrieveAll() {
        return this.localEvents;
    }

    public LocalEvent getBooking(String id, LocalDateTime givenTime) {
        LocalEvent result = null;

        for(var event : retrieveAll()) {
            if(event.Location().equals(id) && isBetweenTime(event,givenTime)) {
                result = event;
            }
        }
        return result;
    }

    @Override
    public List<LocalEvent> getBookingsBy(String location, LocalDate date) {
        return retrieveAll()
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

    @Override
    public void writeTo(Booking booking, User user) {
        var sql = "INSERT INTO Reservation(salle,matricule,jourReservation,debut,fin,description,nbPersonnes) VALUES (?,?,?,?,?,?,?) ";
        try(var stmt = connection.prepareStatement(sql)) {
            //TODO: vérifier si les indexs correspondent
            stmt.setString(1, booking.IdSalle());
            stmt.setString(2, user.Matricule());
            stmt.setDate(3, Date.valueOf(booking.JourReservation()));
            stmt.setTime(4, Time.valueOf(booking.Debut()));
            stmt.setTime(5, Time.valueOf(booking.Fin()));
            stmt.setString(6, booking.Description());
            stmt.setInt(7, booking.NbPersonnes());

            stmt.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
