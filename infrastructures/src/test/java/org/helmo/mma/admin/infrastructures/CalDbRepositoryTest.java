package org.helmo.mma.admin.infrastructures;

import org.helmo.mma.admin.domains.core.Booking;
import org.helmo.mma.admin.domains.core.LocalEvent;
import org.helmo.mma.admin.domains.core.User;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class CalDbRepositoryTest {

    private static final String GIVEN_PATH = "jdbc:sqlite:src/test/resources/dbTests.sqlite";
    private CalDbRepository repo;
    private Connection connection;

    @BeforeEach
    void setUp() {
        try {
            connection = DriverManager.getConnection(GIVEN_PATH);
            for(var query : DbTestUtils.insertAllReservation()){
                connection.createStatement().executeUpdate(query);
            }
            repo = new CalDbRepository(connection);
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.createStatement().execute("DROP TABLE IF EXISTS Reservation");
        connection.close();
    }

    @Test
    void shouldReturnEvents(){
        repo.readTo();
        Map<String,LocalEvent> expected = Map.of(
                "1",new LocalEvent("MAT1234","A101", LocalDate.parse("2024-12-01"), LocalTime.parse("09:00:00"), LocalTime.parse("11:00:00"), "Réunion projet"),
                "2",new LocalEvent("MAT5678","B202",  LocalDate.parse("2024-12-02"), LocalTime.parse("14:00:00"), LocalTime.parse("15:30:00"), "Présentation client")
        );

        assertEquals(expected,repo.retrieveAll());
    }

    @Test
    void shouldWriteEvent(){
        Map<String,LocalEvent> expected = Map.of(
                "1",new LocalEvent("MAT1234","A101", LocalDate.parse("2024-12-01"), LocalTime.parse("09:00:00"), LocalTime.parse("11:00:00"), "Réunion projet"),
                "2",new LocalEvent("MAT5678","B202",  LocalDate.parse("2024-12-02"), LocalTime.parse("14:00:00"), LocalTime.parse("15:30:00"), "Présentation client"),
                "3",new LocalEvent("MAT9999","C303",  LocalDate.parse("2024-12-03"), LocalTime.parse("12:00:00"), LocalTime.parse("14:30:00"), "Bilan")
        );

        var newBooking = new Booking("C303","MAT9999",LocalDate.of(2024,12,3), LocalTime.of(12,0), LocalTime.of(14,30), "Bilan",9);
        var newUser = new User("MAT9999","Vitto","Téodule","t.vitto@helmo.be");
        repo.writeTo(newBooking,newUser);


        assertEquals(expected, repo.retrieveAll());
    }


}