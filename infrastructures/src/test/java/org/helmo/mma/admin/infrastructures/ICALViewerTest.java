package org.helmo.mma.admin.infrastructures;

import org.helmo.mma.admin.domains.core.Booking;
import org.helmo.mma.admin.domains.core.LocalEvent;
import org.helmo.mma.admin.domains.core.User;
import org.helmo.mma.admin.domains.exceptions.BookingException;
import org.helmo.mma.admin.domains.exceptions.CalendarException;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ICALViewerTest {

    private ICALViewer icalViewer;
    private Path pathFile;
    private final String EMPTY_ICAL_CONTENT = """
        BEGIN:VCALENDAR
        VERSION:2.0
        PRODID:-//YourApp//ICAL Tests//EN
        END:VCALENDAR
        """;

    @BeforeEach
    public void setUp() throws IOException {
        // Créer un fichier temporaire pour les tests
        pathFile = Files.createTempFile("testCalendar", ".ics");

        // Écrire le contenu de base d'un fichier iCal vide
        Files.writeString(pathFile, EMPTY_ICAL_CONTENT);

        // Initialiser une nouvelle instance d'ICALViewer avec ce fichier
        icalViewer = new ICALViewer(pathFile.toString());
    }

    @AfterEach
    public void tearDown() throws Exception {
        // Supprimer le fichier temporaire après chaque test
        if (Files.exists(pathFile)) {
            Files.delete(pathFile);
        }
    }
    @Test
    public void testWriteTo_AddsEventToCalendar() {
        // Arrange
        Booking booking = new Booking("Salle101", "123456", LocalDate.now(),
                LocalTime.of(10, 0), LocalTime.of(12, 0), "Réunion importante", 5);
        User user = new User("123456", "Dupont", "Jean", "jean.dupont@example.com");

        // Act
        icalViewer.writeTo(booking, user);

        // Assert
        List<LocalEvent> events = icalViewer.retrieveAll();
        assertEquals(1, events.size());
        LocalEvent event = events.getFirst();
        assertEquals("Dupont_Jean_123456_jean.dupont@example.com", event.Username());
        assertEquals("Salle101", event.Location());
        assertEquals("Réunion importante", event.Summary());
        assertEquals(LocalTime.of(10, 0), event.Debut());
        assertEquals(LocalTime.of(12, 0), event.Fin());
    }

    @Test
    public void testRetrieveAll_ReturnsAllEvents() {
        // Arrange
        Booking booking1 = new Booking("Salle101", "123456", LocalDate.now(),
                LocalTime.of(10, 0), LocalTime.of(12, 0), "Réunion importante", 5);
        Booking booking2 = new Booking("Salle102", "123457", LocalDate.now(),
                LocalTime.of(14, 0), LocalTime.of(15, 0), "Conférence", 10);
        User user = new User("123456", "Dupont", "Jean", "jean.dupont@example.com");

        // Act
        icalViewer.writeTo(booking1, user);
        icalViewer.writeTo(booking2, user);

        // Assert
        List<LocalEvent> events = icalViewer.retrieveAll();
        assertEquals(2, events.size());
    }

    @Test
    public void testGetBooking_ReturnsCorrectEvent() {
        // Arrange
        Booking booking = new Booking("Salle101", "123456", LocalDate.now(),
                LocalTime.of(10, 0), LocalTime.of(12, 0), "Réunion importante", 5);
        User user = new User("123456", "Dupont", "Jean", "jean.dupont@example.com");
        icalViewer.writeTo(booking, user);

        // Act
        LocalEvent event = icalViewer.getBooking("Salle101", LocalTime.of(11, 0));

        // Assert
        assertNotNull(event);
        assertEquals("Salle101", event.Location());
        assertEquals(LocalTime.of(10, 0), event.Debut());
        assertEquals(LocalTime.of(12, 0), event.Fin());
    }

    @Test
    public void testGetBookingsBy_ReturnsEventsForSpecificLocationAndDate() {
        // Arrange
        LocalDate today = LocalDate.now();
        Booking booking1 = new Booking("Salle101", "123456", today,
                LocalTime.of(10, 0), LocalTime.of(12, 0), "Réunion importante", 5);
        Booking booking2 = new Booking("Salle102", "123457", today,
                LocalTime.of(14, 0), LocalTime.of(15, 0), "Conférence", 10);
        User user = new User("123456", "Dupont", "Jean", "jean.dupont@example.com");

        // Act
        icalViewer.writeTo(booking1, user);
        icalViewer.writeTo(booking2, user);
        List<LocalEvent> events = icalViewer.getBookingsBy("Salle101", today);

        // Assert
        assertEquals(1, events.size());
        assertEquals("Salle101", events.get(0).Location());
    }

//    @Test
//    public void testWriteTo_InvalidBooking_ThrowsException() {
//        // Arrange
//        Booking invalidBooking = new Booking("Salle101", "123456", LocalDate.now(),
//                LocalTime.of(10, 0), LocalTime.of(12, 0), "Réunion importante", -5); // NbPersonnes négatif
//        User user = new User("123456", "Dupont", "Jean", "jean.dupont@example.com");
//
//        // Assert
//        assertThrows(BookingException.class, () -> {
//            // Act
//            icalViewer.writeTo(invalidBooking, user);
//        });
//    }

    @Test
    public void testConstructor_ThrowsCalendarException_OnInvalidPath() {
        // Assert
        assertThrows(CalendarException.class, () -> {
            // Act
            new ICALViewer("invalid/path/to/calendar.ics");
        });
    }
}
