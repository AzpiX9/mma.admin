package org.helmo.mma.admin.domains.core;

import org.helmo.mma.admin.domains.exceptions.BookingException;
import org.helmo.mma.admin.domains.exceptions.EventNotFoundException;
import org.helmo.mma.admin.domains.exceptions.RoomException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BookingManagerTest {
    @Test
    void shouldReplaceAllBookings() {
        // Arrange
        Map<String, LocalEvent> initialBookings = new HashMap<>();
        initialBookings.put("1", new LocalEvent("User1", "Room1", LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0), "Meeting"));
        BookingManager manager = new BookingManager(initialBookings);

        Map<String, LocalEvent> newBookings = new HashMap<>();
        newBookings.put("2", new LocalEvent("User2", "Room2", LocalDate.now(), LocalTime.of(11, 0), LocalTime.of(12, 0), "Workshop"));

        // Act
        manager.replaceAll(newBookings);

        // Assert
        assertEquals(1, manager.getBookingsBy("Room2", LocalDate.now()).size());
        assertTrue(manager.getBookingsBy("Room1", LocalDate.now()).isEmpty());
    }

    @Test
    void shouldReturnIdFromReservation() {
        // Arrange
        LocalEvent event = new LocalEvent("User1", "Room1", LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0), "Meeting");
        Map<String, LocalEvent> bookings = Map.of("1", event);
        BookingManager manager = new BookingManager(bookings);

        // Act
        String id = manager.getIdFromReservation(event);

        // Assert
        assertEquals("1", id);
    }

    @Test
    void shouldReturnBookingWhenRoomAndTimeMatch() {
        // Arrange
        LocalEvent event = new LocalEvent("User1", "Room1", LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(12, 0), "Meeting");
        BookingManager manager = new BookingManager(Map.of("1", event));

        // Act
        LocalEvent result = manager.getBooking("Room1", LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 0)));

        // Assert
        assertNotNull(result);
        assertEquals("Room1", result.Location());
        assertEquals("Meeting", result.Summary());
    }

    @Test
    void shouldThrowExceptionWhenRoomDoesNotMatch() {
        // Arrange
        LocalEvent event = new LocalEvent("User1", "Room2", LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(12, 0), "Meeting");
        BookingManager manager = new BookingManager(Map.of("1", event));

        // Act & Assert
        BookingException exception = assertThrows(BookingException.class, () ->
                manager.getBooking("Room1", LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 0)))
        );
        assertEquals("Reservation not found", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenReservationNotFound() {
        // Arrange
        Map<String, LocalEvent> bookings = new HashMap<>();
        BookingManager manager = new BookingManager(bookings);

        LocalEvent nonExistentEvent = new LocalEvent("User1", "Room1", LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0), "Meeting");

        // Act & Assert
        assertThrows(EventNotFoundException.class, () -> manager.getIdFromReservation(nonExistentEvent));
    }

    @Test
    void shouldReturnBookingForGivenRoomAndTime() {
        // Arrange
        LocalEvent event = new LocalEvent("User1", "Room1", LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0), "Meeting");
        Map<String, LocalEvent> bookings = Map.of("1", event);
        BookingManager manager = new BookingManager(bookings);

        // Act
        LocalEvent booking = manager.getBooking("Room1", LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 30)));

        // Assert
        assertEquals(event, booking);
    }

    @Test
    void shouldThrowExceptionWhenBookingNotFound() {
        // Arrange
        Map<String, LocalEvent> bookings = new HashMap<>();
        BookingManager manager = new BookingManager(bookings);

        // Act & Assert
        assertThrows(BookingException.class, () -> manager.getBooking("Room1", LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 30))));
    }

    @Test
    void shouldReturnBookingsByLocationAndDate() {
        // Arrange
        LocalEvent event1 = new LocalEvent("User1", "Room1", LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0), "Meeting");
        LocalEvent event2 = new LocalEvent("User2", "Room2", LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(11, 0), "Workshop");
        Map<String, LocalEvent> bookings = Map.of("1", event1, "2", event2);
        BookingManager manager = new BookingManager(bookings);

        // Act
        List<LocalEvent> room1Bookings = manager.getBookingsBy("Room1", LocalDate.now());

        // Assert
        assertEquals(1, room1Bookings.size());
        assertEquals(event1, room1Bookings.get(0));
    }

    @Test
    void shouldDetectInvalidBookingDueToCollision() throws RoomException {
        // Arrange
        Room room = new Room("Room1", "Main Room", 10);
        LocalEvent existingEvent = new LocalEvent("User1", "Room1", LocalDate.now(),
                LocalTime.of(9, 0), LocalTime.of(10, 0), "Meeting");
        Map<String, LocalEvent> bookings = Map.of("1", existingEvent);
        BookingManager manager = new BookingManager(bookings);

        Booking newBooking = new Booking("Room1", "User123", LocalDate.now(),
                LocalTime.of(9, 30), LocalTime.of(10, 30), "Conflicting Meeting", 5);

        // Act & Assert
        assertThrows(BookingException.class, () -> manager.checkIfNotValid(newBooking, room));
    }

    @Test
    void shouldDetectInvalidBookingDueToCapacity() throws RoomException {
        // Arrange
        Room room = new Room("Room1", "Small Room", 5);
        Booking newBooking = new Booking("Room1", "User123", LocalDate.now(),
                LocalTime.of(9, 0), LocalTime.of(10, 0), "Overbooked Meeting", 10);
        Map<String, LocalEvent> bookings = new HashMap<>();
        BookingManager manager = new BookingManager(bookings);

        // Act & Assert
        assertThrows(BookingException.class, () -> manager.checkIfNotValid(newBooking, room));
    }

    @Test
    void shouldReturnAvailableSlotsForRoom() throws RoomException {
        // Arrange
        LocalEvent event1 = new LocalEvent("User1", "Room1", LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0), "Meeting");
        LocalEvent event2 = new LocalEvent("User2", "Room1", LocalDate.now(), LocalTime.of(11, 0), LocalTime.of(12, 0), "Workshop");
        Map<String, LocalEvent> bookings = Map.of("1", event1, "2", event2);
        BookingManager manager = new BookingManager(bookings);

        // Act
        List<String> availableSlots = manager.checkAvailableOnAll(
                List.of(new Room("Room1", "Conference Room", 10)),
                LocalDate.now(),
                "01:00"
        );

        // Assert
        assertTrue(availableSlots.contains("Room1," + LocalDate.now().plusDays(1) + ",08:00-17:00"));
        //assertTrue(availableSlots.contains("Room1," + LocalDate.now() + ",12:00-17:00"));
        assertFalse(availableSlots.contains("Room1," + LocalDate.now() + ",11:00-12:00"));
    }

    @Test
    void shouldNotThrowWhenBookingIsValid() throws RoomException {
        // Arrange
        LocalEvent existingEvent = new LocalEvent("User1", "Room1", LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0), "Meeting");
        BookingManager manager = new BookingManager(Map.of("1", existingEvent));
        Booking newBooking = new Booking("Room1", "X123456", LocalDate.now(), LocalTime.of(10, 30), LocalTime.of(11, 30), "Team Meeting", 5);
        Room room = new Room("Room1", "Conference Room", 10);

        // Act & Assert
        manager.checkIfNotValid(newBooking, room); // No exception expected
    }

    @Test
    void shouldThrowWhenCollisionDetected() throws RoomException {
        // Arrange
        LocalEvent existingEvent = new LocalEvent("User1", "Room1", LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(11, 0), "Meeting");
        BookingManager manager = new BookingManager(Map.of("1", existingEvent));
        Booking conflictingBooking = new Booking("Room1", "X123456", LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(12, 0), "Workshop", 5);
        Room room = new Room("Room1", "Conference Room", 10);

        // Act & Assert
        BookingException exception = assertThrows(BookingException.class, () ->
                manager.checkIfNotValid(conflictingBooking, room)
        );
        assertEquals("Crénau occupé", exception.getMessage());
    }

    @Test
    void shouldThrowWhenRoomCapacityIsInsufficient() throws RoomException {
        // Arrange
        LocalEvent existingEvent = new LocalEvent("User1", "Room1", LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0), "Meeting");
        BookingManager manager = new BookingManager(Map.of("1", existingEvent));
        Booking largeBooking = new Booking("Room1", "X123456", LocalDate.now(), LocalTime.of(10, 30), LocalTime.of(11, 30), "Team Meeting", 15); // Over capacity
        Room smallRoom = new Room("Room1", "Small Room", 10);

        // Act & Assert
        BookingException exception = assertThrows(BookingException.class, () ->
                manager.checkIfNotValid(largeBooking, smallRoom)
        );
        assertEquals("Capacité insuffisante", exception.getMessage());
    }

    @Test
    void shouldNotThrowWhenNoExistingBookingsForRoom() throws RoomException {
        // Arrange
        BookingManager manager = new BookingManager(Map.of());
        Booking newBooking = new Booking("Room1", "X123456", LocalDate.now(), LocalTime.of(10, 30), LocalTime.of(11, 30), "Team Meeting", 5);
        Room room = new Room("Room1", "Conference Room", 10);

        // Act & Assert
        manager.checkIfNotValid(newBooking, room); // No exception expected
    }
}