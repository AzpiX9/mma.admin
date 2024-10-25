package org.helmo.mma.admin.domains.core;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class LocalEventTest {
    @Test
    public void should_return_a_valid_object() {
        LocalDate date = LocalDate.of(2024, 10, 22);
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(11, 0);

        LocalEvent event = new LocalEvent("johndoe", "Paris", date, startTime, endTime, "Meeting");

        assertEquals("johndoe", event.Username());
        assertEquals("Paris", event.Location());
        assertEquals(date, event.DateJour());
        assertEquals(startTime, event.Debut());
        assertEquals(endTime, event.Fin());
        assertEquals("Meeting", event.Summary());
    }

    @Test
    public void should_return_true_when_two_items_are_equals() {
        LocalDate date = LocalDate.of(2024, 10, 22);
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(11, 0);

        LocalEvent event1 = new LocalEvent("johndoe", "Paris", date, startTime, endTime, "Meeting");
        LocalEvent event2 = new LocalEvent("johndoe", "Paris", date, startTime, endTime, "Meeting");

        assertEquals(event1, event2);
        assertTrue(event1.equals(event2));
        assertTrue(event2.equals(event1));
    }

    @Test
    public void should_return_a_string_representation_with_its_values() {
        LocalDate date = LocalDate.of(2024, 10, 22);
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(11, 0);

        LocalEvent event = new LocalEvent("johndoe", "Paris", date, startTime, endTime, "Meeting");

        String expected = "LocalEvent[Username=johndoe, Location=Paris, DateJour=2024-10-22, Debut=09:00, Fin=11:00, Summary=Meeting]";
        assertEquals(expected, event.toString());
    }

}