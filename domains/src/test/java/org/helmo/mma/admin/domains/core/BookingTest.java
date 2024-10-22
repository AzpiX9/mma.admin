package org.helmo.mma.admin.domains.core;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {
    @Test
    public void should_create_a_booking_object() {
        LocalDate date = LocalDate.of(2024, 10, 22);
        LocalTime startTime = LocalTime.of(14, 0);
        LocalTime endTime = LocalTime.of(16, 0);

        Booking booking = new Booking("LB1", "B23456", date, startTime, endTime, "Réunion de travail", 10);

        assertEquals("LB1", booking.IdSalle());
        assertEquals("B23456", booking.Matricule());
        assertEquals(date, booking.JourReservation());
        assertEquals(startTime, booking.Debut());
        assertEquals(endTime, booking.Fin());
        assertEquals("Réunion de travail", booking.Description());
        assertEquals(10, booking.NbPersonnes());
    }

    @Test
    public void two_records_should_be_equals() {
        LocalDate date = LocalDate.of(2024, 10, 22);
        LocalTime startTime = LocalTime.of(14, 0);
        LocalTime endTime = LocalTime.of(16, 0);

        Booking booking1 = new Booking("LB1", "B23456", date, startTime, endTime, "Réunion de travail", 10);
        Booking booking2 = new Booking("LB1", "B23456", date, startTime, endTime, "Réunion de travail", 10);

        assertEquals(booking1, booking2);
        assertTrue(booking1.equals(booking2));
        assertTrue(booking2.equals(booking1));
    }

    @Test
    public void should_return_a_string_representation_with_its_values() {
        LocalDate date = LocalDate.of(2024, 10, 22);
        LocalTime startTime = LocalTime.of(14, 0);
        LocalTime endTime = LocalTime.of(16, 0);

        Booking booking = new Booking("LB1", "B23456", date, startTime, endTime, "Réunion de travail", 10);

        String expected = "Booking[IdSalle=LB1, Matricule=B23456, JourReservation=2024-10-22, Debut=14:00, Fin=16:00, Description=Réunion de travail, NbPersonnes=10]";
        assertEquals(expected, booking.toString());
    }

    @Test
    public void should_throw_exception_when_negative_number() {
        LocalDate date = LocalDate.of(2024, 10, 22);
        LocalTime startTime = LocalTime.of(14, 0);
        LocalTime endTime = LocalTime.of(16, 0);

        assertThrows(IllegalArgumentException.class, () -> {
            new Booking("LB1", "B23456", date, startTime, endTime, "Réunion de travail", -5);
        });
    }
}