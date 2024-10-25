package org.helmo.mma.admin.domains.booking;

import static org.junit.jupiter.api.Assertions.*;

import org.helmo.mma.admin.domains.core.LocalEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CanReadBookedTest {

    private CanReadBooked canReadBooked;

    @BeforeEach
    void setUp() {
        canReadBooked = mock(CanReadBooked.class);

        LocalEvent event1 = new LocalEvent("B234567", "LC1", LocalDate.of(2024, 10, 23),
                LocalTime.of(10, 0), LocalTime.of(11, 0), "Réunion d'équipe");
        LocalEvent event2 = new LocalEvent("C345678", "LC1", LocalDate.of(2024, 10, 23),
                LocalTime.of(14, 0), LocalTime.of(15, 0), "Présentation projet");

        List<LocalEvent> allEvents = Arrays.asList(event1, event2);

        when(canReadBooked.retrieveAll()).thenReturn(allEvents);
        when(canReadBooked.getBooking("B234567", LocalTime.of(10, 0))).thenReturn(event1);
        when(canReadBooked.getBookingsBy("LC1", LocalDate.of(2024, 10, 23))).thenReturn(allEvents);
    }

    @Test
    void shouldReadData_whenReadToIsCalled() {
        canReadBooked.readTo();
        verify(canReadBooked, times(1)).readTo();
    }

    @Test
    void shouldReturnAllBookings_whenRetrieveAllIsCalled() {
        List<LocalEvent> allBookings = canReadBooked.retrieveAll();
        assertNotNull(allBookings, "Les événements récupérés ne doivent pas être nuls.");
        assertEquals(2, allBookings.size(), "Le nombre d'événements récupérés doit être égal à 2.");
    }

    @Test
    void shouldReturnBooking_whenBookingExistsWithGivenIdAndTime() {
        LocalEvent booking = canReadBooked.getBooking("B234567", LocalTime.of(10, 0));
        assertNotNull(booking, "L'événement récupéré ne doit pas être nul.");
        assertEquals("B234567", booking.Username(), "Le nom d'utilisateur de l'événement doit correspondre.");
        assertEquals(LocalTime.of(10, 0), booking.Debut(), "L'heure de début de l'événement doit correspondre.");
        assertEquals(LocalTime.of(11, 0), booking.Fin(), "L'heure de fin de l'événement doit correspondre.");
        assertEquals("Réunion d'équipe", booking.Summary(), "Le résumé de l'événement doit correspondre.");
    }

    @Test
    void shouldReturnBookings_whenGivenLocationAndDate() {
        List<LocalEvent> bookings = canReadBooked.getBookingsBy("LC1", LocalDate.of(2024, 10, 23));
        assertNotNull(bookings, "La liste des événements ne doit pas être nulle.");
        assertEquals(2, bookings.size(), "Le nombre d'événements doit correspondre à la liste prévue.");
    }

    @Test
    void shouldReturnEmptyList_whenNoBookingsExistForGivenLocationAndDate() {
        when(canReadBooked.getBookingsBy("LC2", LocalDate.of(2024, 10, 23))).thenReturn(Arrays.asList());

        List<LocalEvent> bookings = canReadBooked.getBookingsBy("Lyon", LocalDate.of(2024, 10, 23));
        assertNotNull(bookings, "La liste des événements ne doit pas être nulle.");
        assertTrue(bookings.isEmpty(), "La liste des événements doit être vide.");
    }
}