package org.helmo.mma.admin.infrastructures;

import org.helmo.mma.admin.domains.booking.CalendarRepository;
import org.helmo.mma.admin.domains.core.Booking;
import org.helmo.mma.admin.domains.core.LocalEvent;
import org.helmo.mma.admin.domains.core.User;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class CalDbRepository implements CalendarRepository {

    private Connection connection;
    private Map<String,LocalEvent> localEvents = new LinkedHashMap<>();

    public CalDbRepository(Connection connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void readTo() {
        localEvents.clear();
        var query = "SELECT * FROM Reservation";

        try (Statement statement = connection.createStatement();ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                var localEventTemp = new LocalEvent(
                        resultSet.getString("matricule"),
                        resultSet.getString("salle"),
                        LocalDate.parse(resultSet.getString("jourReservation")),
                        LocalTime.parse(resultSet.getString("debut")),
                        LocalTime.parse(resultSet.getString("fin")),
                        resultSet.getString("description")
                );
                localEvents.put(resultSet.getString("idReservation"),localEventTemp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Map<String,LocalEvent> retrieveAll() {
        readTo();
        return this.localEvents;
    }



    @Override
    public void writeTo(Booking booking, User user) {
        var sql = "INSERT INTO Reservation(salle,matricule,jourReservation,debut,fin,description,nbPersonnes) VALUES (?,?,?,?,?,?,?) ";
        try(var stmt = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            setQueryWithArgs(booking, user, stmt);

            stmt.executeUpdate();
            var localEventTemp = new LocalEvent(user.Matricule(),booking.IdSalle(),Date.valueOf(booking.JourReservation()).toLocalDate(),
                    Time.valueOf(booking.Debut()).toLocalTime(),Time.valueOf(booking.Fin()).toLocalTime(),booking.Description());
            String primaryKey = "";
            try(var rs = stmt.getGeneratedKeys()) {
                if(rs.next()) {
                   primaryKey = rs.getString(1);
                }
            }
            localEvents.put(primaryKey,localEventTemp);

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void setQueryWithArgs(Booking booking, User user, PreparedStatement stmt) throws SQLException {
        stmt.setString(1, booking.IdSalle());
        stmt.setString(2, user.Matricule());
        stmt.setDate(3, Date.valueOf(booking.JourReservation()));
        stmt.setTime(4, Time.valueOf(booking.Debut()));
        stmt.setTime(5, Time.valueOf(booking.Fin()));
        stmt.setString(6, booking.Description());
        stmt.setInt(7, booking.NbPersonnes());
    }
}
