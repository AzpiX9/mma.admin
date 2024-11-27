package org.helmo.mma.admin.infrastructures;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServicesDbRepositoryTest {
    private static final String GIVEN_PATH = "jdbc:sqlite:src/test/resources/dbTests.sqlite";
    private Connection connection;
    private ServicesDbRepository repository;

    @BeforeEach
    void setUp() {
        try {
            connection = DriverManager.getConnection(GIVEN_PATH);
            for (var query : DbTestUtils.insertAllServices()){
                connection.createStatement().executeUpdate(query);
            }
            connection.createStatement().executeUpdate("CREATE TABLE Reservation_Services (\n" +
                    "    idReservation INTEGER NOT NULL,\n" +
                    "    idService INTEGER NOT NULL,\n" +
                    "    FOREIGN KEY (idReservation) REFERENCES Reservation(idReservation)  ON DELETE CASCADE ON UPDATE CASCADE,\n" +
                    "    FOREIGN KEY (idService) REFERENCES Services(idService)  ON DELETE CASCADE ON UPDATE CASCADE\n" +
                    ");");
            repository = new ServicesDbRepository(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void tearDown() {
        try {
            connection.createStatement().executeUpdate("DROP TABLE IF EXISTS Services;");
            connection.createStatement().executeUpdate("DROP TABLE IF EXISTS Reservation_Services;");
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldRetrieveAvailableServicesAndItsSize() {
        // Act
        List<String> services = repository.retrieveAvailableServices();

        // Assert
        assertNotNull(services);
        assertEquals(DbTestUtils.insertAllServices().size() - 1, services.size());
        assertEquals(List.of("Cafe et boissons","Nourriture", "Projecteur", "Matériel Sonore"), services);
    }

    @Test
    void shouldInsertServiceWithReservation() throws SQLException {
        var bookingId = "1";
        var servicesId = List.of("2","3");
        repository.insertReservation(bookingId,servicesId);

        try (var stmt = connection.prepareStatement("SELECT * FROM Reservation_Services WHERE idReservation = ?")) {
            stmt.setInt(1, Integer.parseInt(bookingId));
            try (var rs = stmt.executeQuery()) {
                int rowCount = 0;
                while (rs.next()) {
                    rowCount++;
                    assertEquals(Integer.parseInt(bookingId), rs.getInt("idReservation"));
                    assertTrue(servicesId.contains(String.valueOf(rs.getInt("idService"))));
                }
                assertEquals(servicesId.size(), rowCount, "Le nombre d'insertions doit correspondre au nombre d'ID de service");
            }
        }
    }

    @Test
    void testInsertReservationWithEmptyList() throws SQLException {
        String bookingId = "1";
        List<String> servicesId = List.of();

        repository.insertReservation(bookingId, servicesId);

        try (var stmt = connection.prepareStatement("SELECT * FROM Reservation_Services")) {
            try (var rs = stmt.executeQuery()) {
                assertFalse(rs.next(), "Aucune donnée ne doit être insérée si la liste est vide");
            }
        }
    }

    @Test
    void shouldRetrieveServicesByBookingId() throws SQLException {
        var bookingId = "1";
        var servicesId = List.of("2","3");
        repository.insertReservation(bookingId,servicesId);


        var expected = List.of("Nourriture","Projecteur");
        var actual = repository.retriveServicesFromBooking(bookingId);
        assertEquals(expected, actual);
    }
}