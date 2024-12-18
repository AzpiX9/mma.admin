package org.helmo.mma.admin.infrastructures;

import org.helmo.mma.admin.domains.exceptions.ServiceException;
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
        try (var stmt = connection.prepareStatement("SELECT * FROM Reservation_Services WHERE idReservation = ?")){
            stmt.setString(1, bookingId);
            try(var rs = stmt.executeQuery()) {
                while (rs.next()) {
                    var columnIndex = rs.getString("idService");
                    if(columnIndex == null){
                        return serviceBooked;
                    }
                    var description = retrieveAvailableServices().get(Integer.parseInt(columnIndex)-1);
                    serviceBooked.add(description);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return serviceBooked;
    }


    @Override
    public void insertReservation(String bookingId, List<String> servicesId) {
        if(servicesId == null || servicesId.isEmpty()) {
            insertNullServices(bookingId);
            return;
        }
        for (var serviceId : servicesId) {
            var sql = "INSERT INTO Reservation_Services (idReservation,idService) VALUES (?,?)";
            try (var stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1,Integer.parseInt(bookingId));
                stmt.setInt(2,Integer.parseInt(serviceId));
                stmt.executeUpdate();
            }catch (SQLException e){
                throw new ServiceException(e.getMessage());
            }
        }

    }

    private void insertNullServices(String bookingId) {
        var sql = "INSERT INTO Reservation_Services (idReservation) VALUES (?)";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(bookingId));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new ServiceException(e.getMessage());
        }
    }
}
