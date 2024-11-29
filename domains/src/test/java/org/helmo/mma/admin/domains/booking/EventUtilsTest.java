package org.helmo.mma.admin.domains.booking;

import org.helmo.mma.admin.domains.core.LocalEvent;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventUtilsTest {

    @Test
    void shouldTransformEventsToTimeStrings() {
        // Arrange
        LocalEvent event1 = new LocalEvent("User1", "Room1", LocalDate.now(),
                LocalTime.of(9, 0), LocalTime.of(10, 0), "Meeting");
        LocalEvent event2 = new LocalEvent("User2", "Room2", LocalDate.now(),
                LocalTime.of(11, 0), LocalTime.of(12, 0), "Workshop");
        List<LocalEvent> events = List.of(event1, event2);

        // Act
        List<String> transformed = EventUtils.transform(events);

        // Assert
        assertEquals(2, transformed.size());
        assertTrue(transformed.contains("09:00-10:00"));
        assertTrue(transformed.contains("11:00-12:00"));
    }

    @Test
    void shouldReturnEmptyListWhenNoEventsProvided() {
        // Arrange
        List<LocalEvent> events = List.of();

        // Act
        List<String> transformed = EventUtils.transform(events);

        // Assert
        assertTrue(transformed.isEmpty());
    }

    @Test
    void shouldHandleSingleEventCorrectly() {
        // Arrange
        LocalEvent singleEvent = new LocalEvent("User1", "Room1", LocalDate.now(),
                LocalTime.of(8, 30), LocalTime.of(9, 30), "Morning Meeting");
        List<LocalEvent> events = List.of(singleEvent);

        // Act
        List<String> transformed = EventUtils.transform(events);

        // Assert
        assertEquals(1, transformed.size());
        assertEquals("08:30-09:30", transformed.get(0));
    }

    @Test
    void shouldNotAlterOriginalEventList() {
        // Arrange
        LocalEvent event = new LocalEvent("User1", "Room1", LocalDate.now(),
                LocalTime.of(8, 0), LocalTime.of(9, 0), "Daily Standup");
        List<LocalEvent> events = List.of(event);

        // Act
        List<String> transformed = EventUtils.transform(events);

        // Assert
        assertEquals(1, events.size()); // Original list remains unchanged
        assertEquals("08:00-09:00", transformed.get(0));
    }
}
