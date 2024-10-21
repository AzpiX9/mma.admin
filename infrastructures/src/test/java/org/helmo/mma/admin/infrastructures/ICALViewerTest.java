package org.helmo.mma.admin.infrastructures;

import org.helmo.mma.admin.domains.core.Booking;
import org.helmo.mma.admin.domains.core.LocalEvent;
import org.helmo.mma.admin.domains.core.User;
import org.helmo.mma.admin.domains.exceptions.CalendarException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.property.immutable.ImmutableVersion;
import net.fortuna.ical4j.data.CalendarOutputter;

import static org.junit.jupiter.api.Assertions.*;

public class ICALViewerTest {

    private static final String TEST_FILE_PATH = "test_calendar.ics";
    private ICALViewer viewer;

    @BeforeEach
    public void setUp() throws Exception {
        // Créer le fichier iCal s'il n'existe pas déjà
        createTestICalFile();

        // Initialiser le viewer avec le fichier iCal existant
        viewer = new ICALViewer(TEST_FILE_PATH);
    }

    @AfterEach
    public void tearDown() {
        // Supprimer le fichier iCal après chaque test
        File file = new File(TEST_FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
    }

    private void createTestICalFile() throws IOException {
        File file = new File(TEST_FILE_PATH);

        // Si le fichier n'existe pas, on le crée avec un calendrier vide
        if (!file.exists()) {
            Calendar calendar = new Calendar();
            calendar.add(ImmutableVersion.VERSION_2_0);

            try (FileOutputStream fout = new FileOutputStream(file)) {
                new CalendarOutputter().output(calendar, fout);
            }
        }
    }

    @Test
    public void testWriteTo_WithValidData() {
        // Créer un utilisateur et une réservation fictive
        User user = new User("B1234", "John", "Doe", "john.doe@example.com");
        Booking booking = new Booking("Room101", "B1234", LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(11, 0), "Meeting", 5);

        // Appeler la méthode writeTo
        viewer.writeTo(booking, user);

        // Lire à nouveau pour vérifier si l'événement est bien ajouté
        List<LocalEvent> events = viewer.retrieveAll();
        assertFalse(events.isEmpty(), "L'événement n'a pas été ajouté au fichier iCal");

        LocalEvent event = events.get(0);
        assertEquals("Room101", event.Location());
        assertEquals("Meeting", event.Summary());
        assertEquals(LocalTime.of(10, 0), event.Debut());
    }

    @Test
    public void testWriteTo_InvalidFilePath_ShouldThrowCalendarException() {
        // Créer un viewer avec un fichier iCal non existant ou corrompu
        ICALViewer invalidViewer = new ICALViewer("invalid_path.ics");

        User user = new User("C9999", "Alice", "Wonderland", "alice.wonderland@example.com");
        Booking booking = new Booking("Room404", "C9999", LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0), "Error test", 1);

        // Vérifier si CalendarException est levé
        var exception = assertThrows(CalendarException.class, () -> invalidViewer.writeTo(booking, user));
        assertEquals("org.helmo.mma.admin.domains.exceptions.CalendarException -> invalid_path.ics",exception);
    }

    @Test
    public void testReadTo_WithEmptyFile_ShouldReturnNoEvents() {
        // Lire le fichier iCal qui est vide
        viewer.readTo();

        List<LocalEvent> events = viewer.retrieveAll();
        assertTrue(events.isEmpty(), "Aucun événement ne devrait être présent dans un fichier iCal vide.");
    }

    @Test
    public void testGetBooking_WithNoMatchingEvent_ShouldReturnNull() {
        // Créer un utilisateur et une réservation fictive
        User user = new User("A5678", "Jane", "Smith", "jane.smith@example.com");
        Booking booking = new Booking("Room202", "A5678", LocalDate.now(), LocalTime.of(14, 0), LocalTime.of(15, 0), "Conference", 10);

        // Ajouter un événement à un fichier iCal existant
        viewer.writeTo(booking, user);

        // Chercher un événement avec un ID qui ne correspond pas
        LocalEvent event = viewer.getBooking("Room404", LocalTime.of(14, 30));
        assertNull(event, "Aucun événement ne devrait être trouvé avec un ID non correspondant.");
    }

    @Test
    public void testGetBooking_WithMatchingEvent_ShouldReturnEvent() {
        // Créer un utilisateur et une réservation fictive
        User user = new User("B4321", "John", "Doe", "john.doe@example.com");
        Booking booking = new Booking("Room303", "B4321", LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0), "Workshop", 15);

        // Ajouter un événement au fichier iCal
        viewer.writeTo(booking, user);

        // Chercher l'événement par ID et heure
        LocalEvent event = viewer.getBooking("Room303", LocalTime.of(9, 30));
        assertNotNull(event, "L'événement devrait être trouvé pour cette plage horaire");
        assertEquals("Workshop", event.Summary());
    }

    @Test
    public void testGetBookingsBy_WithMultipleEvents_ShouldReturnFilteredResults() {
        // Créer plusieurs événements et vérifier leur récupération par emplacement et date
        User user = new User("C1234", "Alice", "Brown", "alice.brown@example.com");
        Booking booking1 = new Booking("Room404", "C1234", LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0), "Training", 8);
        Booking booking2 = new Booking("Room404", "C5678", LocalDate.now(), LocalTime.of(11, 0), LocalTime.of(12, 0), "Discussion", 12);

        viewer.writeTo(booking1, user);
        viewer.writeTo(booking2, user);

        List<LocalEvent> events = viewer.getBookingsBy("Room404", LocalDate.now());
        assertEquals(2, events.size(), "Deux événements devraient être présents pour cette salle et cette date");
    }

    @Test
    public void testRetrieveAll_ShouldReturnAllEvents() {
        // Créer plusieurs événements pour tester retrieveAll
        User user = new User("C1234", "Alice", "Brown", "alice.brown@example.com");
        Booking booking1 = new Booking("Room404", "C1234", LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0), "Training", 8);
        Booking booking2 = new Booking("Room404", "C5678", LocalDate.now(), LocalTime.of(11, 0), LocalTime.of(12, 0), "Discussion", 12);

        viewer.writeTo(booking1, user);
        viewer.writeTo(booking2, user);

        List<LocalEvent> events = viewer.retrieveAll();
        assertEquals(2, events.size(), "Il devrait y avoir deux événements récupérés.");
    }

    @Test
    public void testParseToBooking_ShouldCorrectlyParseVEvent() {
        // Test parsing de VEvent à LocalEvent pour garantir la correspondance des données
        User user = new User("D6789", "Bob", "Builder", "bob.builder@example.com");
        Booking booking = new Booking("Room505", "D6789", LocalDate.now(), LocalTime.of(15, 0), LocalTime.of(16, 0), "Construction meeting", 3);

        viewer.writeTo(booking, user);
        viewer.readTo();

        List<LocalEvent> events = viewer.retrieveAll();
        assertFalse(events.isEmpty(), "Un événement devrait avoir été ajouté.");

        LocalEvent event = events.get(0);
        assertEquals("Room505", event.Location(), "Le lieu de l'événement doit correspondre.");
        assertEquals("Construction meeting", event.Summary(), "La description doit correspondre.");
    }
}
