package org.helmo.mma.admin.infrastructures;

import org.helmo.mma.admin.domains.core.Booking;
import org.helmo.mma.admin.domains.core.LocalEvent;
import org.helmo.mma.admin.domains.core.User;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

class ICALViewerTest {

    private Path tempFile;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = Files.createTempFile("test-calendar", ".ics");

        // Initialisation minimale du fichier iCal
        try (var writer = Files.newBufferedWriter(tempFile)) {
            writer.write("BEGIN:VCALENDAR\n");
            writer.write("VERSION:2.0\n");
            writer.write("END:VCALENDAR\n");
        }
    }

    @AfterEach
    void tearDown() throws IOException {
        // Suppression du fichier temporaire
        Files.deleteIfExists(tempFile);
    }

    @Test
    void shouldInitializeICALViewerSuccessfully() {
        // Act
        ICALViewer viewer = new ICALViewer(tempFile.toString());

        // Assert
        assertNotNull(viewer);
    }

    @Test
    void shouldWriteBookingToFile() {
        // Arrange
        ICALViewer viewer = new ICALViewer(tempFile.toString());
        Booking booking = new Booking("Room1", "12345", LocalDate.now().plusDays(1),
                LocalTime.of(10, 0), LocalTime.of(11, 0),
                "Team Meeting", 5);
        User user = new User("A123456","John", "Doe", "john.doe@example.com");

        // Act
        viewer.writeTo(booking, user);

        // Assert
        Map<String, LocalEvent> events = viewer.retrieveAll();
        assertEquals(1, events.size());
        LocalEvent event = events.values().iterator().next();
        assertEquals("Room1", event.Location());
        assertEquals(LocalDate.now().plusDays(1), event.DateJour());
        assertEquals(LocalTime.of(10, 0), event.Debut());
        assertEquals(LocalTime.of(11, 0), event.Fin());
        assertEquals("Team Meeting", event.Summary());
    }

    @Test
    void shouldNotWritePastBookingsToFile() {
        // Arrange
        ICALViewer viewer = new ICALViewer(tempFile.toString());
        Booking booking = new Booking("Room1", "12345", LocalDate.now().minusDays(1),
                LocalTime.of(10, 0), LocalTime.of(11, 0),
                "Past Meeting", 5);
        User user = new User("A123456","John", "Doe", "john.doe@example.com");

        // Act
        viewer.writeTo(booking, user);

        // Assert
        Map<String, LocalEvent> events = viewer.retrieveAll();
        assertTrue(events.isEmpty());
    }

    @Test
    void shouldReadEventsFromFile() {
        // Arrange
        ICALViewer viewer = new ICALViewer(tempFile.toString());
        Booking booking = new Booking("Room1", "12345", LocalDate.now().plusDays(1),
                LocalTime.of(10, 0), LocalTime.of(11, 0),
                "Team Meeting", 5);
        User user = new User("A123456","John", "Doe", "john.doe@example.com");
        viewer.writeTo(booking, user);

        // Act
        viewer.readTo();
        Map<String, LocalEvent> events = viewer.retrieveAll();

        // Assert
        assertEquals(1, events.size());
        LocalEvent event = events.values().iterator().next();
        assertEquals("Room1", event.Location());
        assertEquals(LocalDate.now().plusDays(1), event.DateJour());
        assertEquals(LocalTime.of(10, 0), event.Debut());
        assertEquals(LocalTime.of(11, 0), event.Fin());
        assertEquals("Team Meeting", event.Summary());
    }

    @Test
    void shouldParseVEventToLocalEvent() {
        // Arrange
        ICALViewer viewer = new ICALViewer(tempFile.toString());
        Booking booking = new Booking("Room1", "12345", LocalDate.now().plusDays(1),
                LocalTime.of(10, 0), LocalTime.of(11, 0),
                "Team Meeting", 5);
        User user = new User("A123456","John", "Doe", "john.doe@example.com");
        viewer.writeTo(booking, user);

        // Act
        LocalEvent event = viewer.retrieveAll().values().iterator().next();

        // Assert
        assertNotNull(event);
        assertEquals("Room1", event.Location());
        assertEquals(LocalDate.now().plusDays(1), event.DateJour());
        assertEquals(LocalTime.of(10, 0), event.Debut());
        assertEquals(LocalTime.of(11, 0), event.Fin());
        assertEquals("Team Meeting", event.Summary());
    }
}
