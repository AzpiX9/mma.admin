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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

//TODO: Faire bcp de tests (dépasser 80%)
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
    public void tearDown() throws IOException {
        // Supprimer le fichier temporaire après chaque test
        if (Files.exists(pathFile)) {
            Files.delete(pathFile);
        }
    }

    @Test
    public void shouldWriteTo_AddNewBookingToCalendar() {
        // Arrange
        Booking booking = new Booking("Salle101", "123456", LocalDate.now(),
                LocalTime.of(10, 0), LocalTime.of(12, 0), "Réunion importante", 5);
        User user = new User("123456", "Dupont", "Jean", "jean.dupont@example.com");

        // Act
        icalViewer.writeTo(booking, user);

        // Assert
        List<LocalEvent> events = icalViewer.retrieveAll();
        assertEquals(1, events.size());
        LocalEvent event = events.get(0);
        assertEquals("Salle101", event.Location());
        assertEquals(LocalTime.of(10, 0), event.Debut());
        assertEquals(LocalTime.of(12, 0), event.Fin());
        assertEquals("Réunion importante", event.Summary());
    }

    @Test
    public void shouldGetBooking_ReturnCorrectEvent() {
        // Arrange
        Booking booking = new Booking("Salle101", "123456", LocalDate.now(),
                LocalTime.of(10, 0), LocalTime.of(12, 0), "Réunion importante", 5);
        User user = new User("123456", "Dupont", "Jean", "jean.dupont@example.com");
        icalViewer.writeTo(booking, user);


        // Act
        LocalEvent retrievedEvent = icalViewer.getBooking("Salle101", LocalDateTime.of(LocalDate.now(),LocalTime.of(11, 0)));

        // Assert
        assertNotNull(retrievedEvent);
        assertEquals("Salle101", retrievedEvent.Location());
        assertEquals(LocalTime.of(10, 0), retrievedEvent.Debut());
        assertEquals(LocalTime.of(12, 0), retrievedEvent.Fin());
    }

    @Test
    public void shouldReturnAllBookingsForGivenLocationAndDate() {
        // Arrange
        Booking booking1 = new Booking("Salle101", "123456", LocalDate.now(),
                LocalTime.of(10, 0), LocalTime.of(12, 0), "Réunion importante", 5);
        Booking booking2 = new Booking("Salle101", "789012", LocalDate.now(),
                LocalTime.of(14, 0), LocalTime.of(16, 0), "Réunion brainstorming", 8);
        User user = new User("123456", "Dupont", "Jean", "jean.dupont@example.com");

        // Act
        icalViewer.writeTo(booking1, user);
        icalViewer.writeTo(booking2, user);

        List<LocalEvent> bookings = icalViewer.getBookingsBy("Salle101", LocalDate.now());

        // Assert
        assertEquals(2, bookings.size());
        assertEquals("Réunion importante", bookings.get(0).Summary());
        assertEquals("Réunion brainstorming", bookings.get(1).Summary());
    }

    @Test
    public void shouldNotGetBooking_WhenTimeOutsideEvent() {
        // Arrange
        Booking booking = new Booking("Salle101", "123456", LocalDate.now(),
                LocalTime.of(10, 0), LocalTime.of(12, 0), "Réunion importante", 5);
        User user = new User("123456", "Dupont", "Jean", "jean.dupont@example.com");
        icalViewer.writeTo(booking, user);

        // Act
        LocalEvent retrievedEvent = icalViewer.getBooking("Salle101", LocalDateTime.of(LocalDate.now(),LocalTime.of(13, 0)));

        // Assert
        assertNull(retrievedEvent);
    }

    @Test
    public void shouldNotAddEventInPast(){
        Booking booking = new Booking("Salle101", "123456", LocalDate.now().minusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0), "Réunion importante", 5);
        User user = new User("123456", "Dupont", "Jean", "jean.dupont@example.com");
        icalViewer.writeTo(booking, user);

        LocalEvent retrievedEvent = icalViewer.getBooking("Salle101", LocalDateTime.of(LocalDate.now(),LocalTime.of(11, 0)));

        assertNull(retrievedEvent);
    }

    @Test
    public void shouldReturnAnEmptyWhenLocationAndDateIsNotValid(){
        var result = icalViewer.getBookingsBy("Salle101", LocalDate.now());

        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldThrowCalendarException_WhenFileDoesNotExist() {
        // Suppression du fichier temporaire pour simuler un fichier inexistant
        assertDoesNotThrow(() -> Files.deleteIfExists(pathFile));

        // Vérifier que l'appel à readTo() lève une CalendarException avec le bon message
        CalendarException exception = assertThrows(CalendarException.class, () -> icalViewer.readTo());
        assertEquals("CalendarException -> Ce calendrier n'existe pas", exception.getMessage());
    }

    @Test
    public void shouldThrowCalendarException_WhenFileIsCorrupted() throws IOException {
        // Écrire un contenu corrompu dans le fichier
        Files.writeString(pathFile, "INVALID CONTENT");

        // Vérifier que l'appel à readTo() lève une CalendarException avec le bon message
        CalendarException exception = assertThrows(CalendarException.class, () -> icalViewer.readTo());
        assertEquals("CalendarException -> Les composants sont invalides", exception.getMessage());
    }

    @Test
    public void shouldIgnoreNonVEventComponents_WhenReadingFromFile() {
        // Simuler un fichier contenant un composant non-VEvent (comme un VTODO ou VTIMEZONE)
        String icalContentWithNonVEvent = """
        BEGIN:VCALENDAR
        VERSION:2.0
        PRODID:-//YourApp//ICAL Tests//EN
        BEGIN:VEVENT
        SUMMARY:Réunion de test
        LOCATION:Salle101
        DTSTART;TZID=Europe/Paris:20241023T100000
        DTEND;TZID=Europe/Paris:20241023T120000
        ORGANIZER;CN=Jean Dupont:MAILTO:jean.dupont@example.com
        END:VEVENT
        BEGIN:VTODO
        SUMMARY:Faire un rapport
        DUE;TZID=Europe/Paris:20241023T170000
        END:VTODO
        END:VCALENDAR
        """;

        assertDoesNotThrow(() -> Files.writeString(pathFile, icalContentWithNonVEvent));

        // Appeler la méthode readTo() et vérifier que seul le composant VEvent est ajouté
        assertDoesNotThrow(() -> icalViewer.readTo());
        List<LocalEvent> events = icalViewer.retrieveAll();

        // Vérifier que seul l'événement VEvent est ajouté, et non le composant VTODO
        assertEquals(1, events.size());
        assertEquals("Réunion de test", events.get(0).Summary());
        assertEquals("Salle101", events.get(0).Location());
    }

    @Test
    public void shouldReturnEmptyList_WhenNoBookingsFound() {
        // Vérifier que la liste retournée est vide si aucun événement n'a été ajouté
        List<LocalEvent> bookings = icalViewer.getBookingsBy("Salle101", LocalDate.now());
        assertTrue(bookings.isEmpty(), "La liste de réservations devrait être vide.");
    }

    @Test
    public void shouldReturnEmptyList_WhenNoMatchingBookingsFound() {

        String validEventContent = """
        BEGIN:VCALENDAR
        VERSION:2.0
        PRODID:-//YourApp//ICAL Tests//EN
        BEGIN:VEVENT
        SUMMARY:Réunion de test
        LOCATION:Salle202
        DTSTART;TZID=Europe/Paris:20241023T100000
        DTEND;TZID=Europe/Paris:20241023T120000
        ORGANIZER;CN=Jean Dupont:MAILTO:jean.dupont@example.com
        END:VEVENT
        END:VCALENDAR
        """;
        assertDoesNotThrow(() -> Files.writeString(pathFile, validEventContent));

        assertDoesNotThrow(() -> icalViewer.readTo());

        List<LocalEvent> bookings = icalViewer.getBookingsBy("Salle101", LocalDate.now());
        assertTrue(bookings.isEmpty(), "La liste de réservations devrait être vide.");
    }

}
