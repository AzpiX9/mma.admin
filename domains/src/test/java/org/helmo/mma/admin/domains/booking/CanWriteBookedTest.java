package org.helmo.mma.admin.domains.booking;

import org.helmo.mma.admin.domains.core.*;
import org.helmo.mma.admin.domains.exceptions.BookingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CanWriteBookedTest {

    private CanWriteBooked canWriteBooked;

    @BeforeEach
    void setUp() {
        canWriteBooked = mock(CanWriteBooked.class);
    }

    @Test
    void shouldWriteBooking_whenValidBookingAndUserProvided() {
        User user = new User("D456789", "Doe", "John", "john.doe@example.com");
        Booking booking = new Booking("M205", "D456789", LocalDate.of(2024, 10, 23),
                LocalTime.of(10, 0), LocalTime.of(11, 0), "Réunion de projet", 10);

        canWriteBooked.writeTo(booking, user);

        verify(canWriteBooked, times(1)).writeTo(booking, user);
    }

    @Test
    void shouldThrowException_whenBookingWithNegativePersons() {
        User user = new User("D456789", "Doe", "John", "john.doe@example.com");

        Exception exception = assertThrows(BookingException.class, () -> {
            new Booking("M205", "D456789", LocalDate.of(2024, 10, 23),
                    LocalTime.of(10, 0), LocalTime.of(11, 0), "Réunion de projet", -5);
        });

        assertEquals("Nombre de personne ne peut pas être négatif", exception.getMessage());
    }

    @Test
    void shouldThrowException_whenUserWithInvalidEmail() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new User("D456789", "Doe", "John", "john.doe@example");
        });

        assertEquals("L'email est invalide.", exception.getMessage());
    }

    @Test
    void shouldThrowException_whenUserWithBlankMatricule() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new User("", "Doe", "John", "john.doe@example.com");
        });

        assertEquals("Le matricule ne peut pas être nul ou vide.", exception.getMessage());
    }

    @Test
    void shouldCaptureArguments_whenWriteToIsCalled() {
        User user = new User("D456789", "Doe", "John", "john.doe@example.com");
        Booking booking = new Booking("M205", "D456789", LocalDate.of(2024, 10, 23),
                LocalTime.of(10, 0), LocalTime.of(11, 0), "Réunion de projet", 10);

        canWriteBooked.writeTo(booking, user);

        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(canWriteBooked).writeTo(bookingCaptor.capture(), userCaptor.capture());

        assertEquals("M205", bookingCaptor.getValue().IdSalle());
        assertEquals("D456789", userCaptor.getValue().Matricule());
        assertEquals("john.doe@example.com", userCaptor.getValue().Email());
    }
}