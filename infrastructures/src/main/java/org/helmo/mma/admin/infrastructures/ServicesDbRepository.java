package org.helmo.mma.admin.infrastructures;

import org.helmo.mma.admin.domains.services.SevicesRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ServicesDbRepository implements SevicesRepository {

    private final Connection connection;

    public ServicesDbRepository(Connection connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public List<String> retrieveAvailableServices() {
        var allServices = new ArrayList<String>();
        var sql = "SELECT * FROM Services";
        try(var stmt = connection.prepareStatement(sql);var rs = stmt.executeQuery()) {
            while (rs.next()) {
                allServices.add(rs.getString(2));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return allServices;
    }

    @Override
    public List<String> retriveServicesFromBooking(String bookingId) {
        var serviceBooked = new ArrayList<String>();
        var sql = "SELECT * FROM Reservation_Services WHERE idReservation = ?";
        try (var stmt = connection.prepareStatement(sql)){
            stmt.setString(1, bookingId);
            try(var rs = stmt.executeQuery()) {
                while (rs.next()) {
                    var index = Integer.parseInt(rs.getString(2));
                    var description = retrieveAvailableServices().get(index-1);
                    serviceBooked.add(description);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return serviceBooked;
    }


    @Override
    public void insertReservation(String bookingId, List<String> services) {
        if(services.isEmpty()) {
            return;
        }
        for (var service : services) {
            var sql = "INSERT INTO Reservation_Services (idReservation,idService) VALUES (?,?)";
            try (var stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1,Integer.parseInt(bookingId));
                stmt.setInt(2,Integer.parseInt(service));
                stmt.executeUpdate();
            }catch (SQLException e){
                throw new RuntimeException(e);
            }
        }

    }
}
