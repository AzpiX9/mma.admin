package org.helmo.mma.admin.domains.booking;

import org.helmo.mma.admin.domains.core.LocalEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AvailableSlotServiceTest {


    private final List<LocalEvent> eventList = new ArrayList<>();
    @BeforeEach
    void setUp() {
        eventList.add(new LocalEvent("Bob", "Lyon", LocalDate.of(2024, 10, 22), LocalTime.of(10, 0), LocalTime.of(11, 0), "Présentation projet"));
        eventList.add(new LocalEvent("Bob", "Lyon", LocalDate.of(2024, 10, 21), LocalTime.of(11, 30), LocalTime.of(12, 30), "Présentation projet"));
        eventList.add(new LocalEvent("Bob", "Lyon", LocalDate.of(2024, 10, 21), LocalTime.of(13, 0), LocalTime.of(14, 0), "Présentation projet"));
        eventList.add(new LocalEvent("Bob", "Lyon", LocalDate.of(2024, 10, 21), LocalTime.of(15, 30), LocalTime.of(16, 30), "Présentation projet"));
    }

    @AfterEach
    void tearDown() {
        eventList.clear();
    }

    @Test
    public void should_returns_size_from_given_list() {
        var slots = new AvailableSlotService(eventList);

        assertEquals(4,eventList.size());
    }

    @Test
    public void should_returns_slot_from_given_events() {
        var slots = new AvailableSlotService(eventList);

        var expected = "8:00-10:00";
        assertEquals(expected,slots.computeTimeSlot());
    }

    @Test
    public void should_returns_whole_working_day_from_empty_events() {
        var slots = new AvailableSlotService(new ArrayList<>());

        var expected = "8:00-17:00";
        assertEquals(expected,slots.computeTimeSlot());
    }

    @Test
    public void should_returns_whole_working_day_from_null() {
        var slots = new AvailableSlotService(null);

        var expected = "8:00-17:00";
        assertEquals(expected,slots.computeTimeSlot());
    }
    @Test
    public void should_returns_difference_of_times() {
        var slots = new AvailableSlotService(eventList);

        var expected = "8:00-10:00";
        var timeDiffExpected = LocalTime.of(2, 0);
        assertEquals(expected,slots.computeTimeSlot());
        assertEquals(timeDiffExpected,slots.getTimeDifference());
    }


}