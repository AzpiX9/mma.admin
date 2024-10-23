package org.helmo.mma.admin.presentations;

import org.helmo.mma.admin.domains.booking.CalendarRepository;
import org.helmo.mma.admin.domains.core.*;
import org.helmo.mma.admin.domains.exceptions.*;
import org.helmo.mma.admin.domains.rooms.CanReadRooms;
import org.helmo.mma.admin.domains.users.CanReadUsers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MainPresenterTest {

    private MainView mockView;
    private CanReadRooms mockRoomsRepo;
    private CanReadUsers mockUsersRepo;
    private CalendarRepository mockCalendarRepo;
    private MainPresenter presenter;

    @BeforeEach
    void setUp() {
        mockView = mock(MainView.class);
        mockRoomsRepo = mock(CanReadRooms.class);
        mockUsersRepo = mock(CanReadUsers.class);
        mockCalendarRepo = mock(CalendarRepository.class);

        BookingAggregator mockAggregator = mock(BookingAggregator.class);
        when(mockAggregator.getRoomsRepo()).thenReturn(mockRoomsRepo);
        when(mockAggregator.getUsersRepo()).thenReturn(mockUsersRepo);
        when(mockAggregator.getCalendarRepository()).thenReturn(mockCalendarRepo);

        presenter = new MainPresenter(mockView, mockAggregator);
    }

    @Test
    void shouldDisplayBookingsForGivenDateWhenSeeBookingRequestIsCalled() {
        // Arrange
        LocalDate date = LocalDate.now();
        Room room = new Room("Room1", "Salle A", 10);
        List<Room> rooms = List.of(room);
        List<LocalEvent> events = List.of(new LocalEvent("John Doe", "Salle A", date,
                LocalTime.of(10, 0), LocalTime.of(11, 0), "Réunion"));

        when(mockRoomsRepo.getRooms()).thenReturn(rooms);
        when(mockCalendarRepo.getBookingsBy(room.Id(), date)).thenReturn(events);

        // Act
        presenter.seeBookingRequest(date);

        // Assert
        verify(mockView).displayByLocalEvs(eq(room.Id()), eq(List.of("10:00-11:00")));
    }

    @Test
    void shouldDisplayErrorWhenRoomCapacityIsExceeded() {
        // Arrange
        String request = "Room1, Mat123, 2024-10-22, 10:00, 11:00, Réunion, 15";
        Booking booking = new Booking("Room1", "Mat123", LocalDate.parse("2024-10-22"),
                LocalTime.of(10, 0), LocalTime.of(11, 0), "Réunion", 15);

        Room room = new Room("Room1", "Salle A", 10);
        when(mockRoomsRepo.getRoom("Room1")).thenReturn(room);

        // Act
        presenter.writeEventRequest(request);

        // Assert
        verify(mockView).displayError("Capacité insuffisante");
    }

    @Test
    void shouldWriteEventWhenBookingIsValid() {
        // Arrange
        String request = "Room1, Mat123, 2024-10-22, 10:00, 11:00, Réunion, 5";
        Booking booking = new Booking("Room1", "Mat123", LocalDate.parse("2024-10-22"),
                LocalTime.of(10, 0), LocalTime.of(11, 0), "Réunion", 5);

        Room room = new Room("Room1", "Salle A", 10);
        User booker = new User("Mat123", "John", "Doe", "john.doe@example.com");

        when(mockRoomsRepo.getRoom("Room1")).thenReturn(room);
        when(mockUsersRepo.getUser("Mat123")).thenReturn(booker);
        when(mockUsersRepo.exists("Mat123")).thenReturn(true);
        when(mockCalendarRepo.getBookingsBy("Room1", LocalDate.parse("2024-10-22"))).thenReturn(List.of());

        // Act
        presenter.writeEventRequest(request);

        // Assert
        verify(mockCalendarRepo).writeTo(booking, booker);
    }

    @Test
    void shouldDisplayErrorWhenBookingNotFoundInViewRequest() {
        // Arrange
        String request = "query, Room1, 10:00";

        when(mockCalendarRepo.getBooking("Room1", LocalTime.of(10, 0))).thenReturn(null);

        // Act
        presenter.viewRequest(request);

        // Assert
        verify(mockView).displayError("Aucune correspondance trouvée");
    }

    @Test
    void shouldDisplayAvailableRoomsWhenRequestIsValid() {
        // Arrange
        String request = "2024-10-22, 5, 01:00";
        Room room = new Room("Room1", "Salle A", 10);
        List<Room> rooms = List.of(room);

        var list = List.of(
                new LocalEvent("Room1", "OtherUser", LocalDate.parse("2024-10-22"), LocalTime.of(9, 0), LocalTime.of(10, 30), "Réunion"));

        when(mockRoomsRepo.getRooms()).thenReturn(rooms);
        when(mockCalendarRepo.getBookingsBy("Room1", LocalDate.parse("2024-10-22"))).thenReturn(list);

        // Act
        presenter.availableRequest(request);

        // Assert
        ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockView).displayAvailable(captor.capture());

        List<String> capturedSlots = captor.getValue();
        assertEquals(1, capturedSlots.size());
        assertEquals("Room1 | 2024-10-22 | 11:00 | 17:00 |", capturedSlots.getFirst()); // Example formatting
    }

    @Test
    void shouldDisplayErrorWhenUserDoesNotExistInWriteEventRequest() {
        // Arrange
        String request = "Room1, Mat123, 2024-10-22, 10:00, 11:00, Réunion, 5";

        Room room = new Room("Room1", "Salle A", 10);
        when(mockRoomsRepo.getRoom("Room1")).thenReturn(room);
        when(mockUsersRepo.exists("Mat123")).thenReturn(false);

        // Act
        presenter.writeEventRequest(request);

        // Assert
        verify(mockView).displayError("Utilisateur non trouvé");
    }

    @Test
    void shouldDisplayErrorWhenEventCollides() {
        // Arrange
        String request = "Room1, Mat123, 2024-10-22, 10:00, 11:00, Réunion, 5";
        Booking booking = new Booking("Room1", "Mat123", LocalDate.parse("2024-10-22"),
                LocalTime.of(10, 0), LocalTime.of(11, 0), "Réunion", 5);

        Room room = new Room("Room1", "Salle A", 10);
        User booker = new User("Mat123", "John", "Doe", "john.doe@example.com");

        var list = List.of(
                new LocalEvent("Room1", "OtherUser", LocalDate.parse("2024-10-22"), LocalTime.of(9, 0), LocalTime.of(10, 30), "Réunion"));
        when(mockRoomsRepo.getRoom("Room1")).thenReturn(room);
        when(mockUsersRepo.exists("Mat123")).thenReturn(true);
        when(mockCalendarRepo.getBookingsBy("Room1", LocalDate.parse("2024-10-22"))).thenReturn(list);

        // Act
        presenter.writeEventRequest(request);

        // Assert
        verify(mockView).displayError("Crénau occupé");
    }

    @Test
    void shouldDisplayAvailableSlotsWhenNoBookingsInAvailableRequest() {
        // Arrange
        String request = "2024-10-22, 10, 01:00";
        Room room = new Room("Room1", "Salle A", 10);
        List<Room> rooms = List.of(room);
      
        when(mockCalendarRepo.getBookingsBy("Room1", LocalDate.parse("2024-10-22"))).thenReturn(List.of());

        // Act
        presenter.availableRequest(request);

        // Assert
        ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockView).displayAvailable(captor.capture());

        List<String> capturedSlots = captor.getValue();
        assertTrue(capturedSlots.isEmpty(), "Les créneaux disponibles ne devraient pas être vides");
    }

    @Test
    void shouldHandleMultipleAvailableSlotsCorrectly() {
        // Arrange
        String request = "2024-10-22, 5, 01:00";
        Room room = new Room("Room1", "Salle A", 10);
        List<Room> rooms = List.of(room);
        LocalDate date = LocalDate.parse("2024-10-22");

        
        when(mockRoomsRepo.getRooms()).thenReturn(rooms);
        when(mockCalendarRepo.getBookingsBy("LB1", date)).thenReturn(List.of(
                new LocalEvent("A123456", "LB1", date, LocalTime.of(9, 0), LocalTime.of(10, 0), "Réunion"),
                new LocalEvent("A123456", "LB1", date, LocalTime.of(11, 0), LocalTime.of(12, 0), "Réunion")
        ));

        // Act
        presenter.availableRequest(request);

        // Assert
        ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockView).displayAvailable(captor.capture());

        List<String> capturedSlots = captor.getValue();
        assertEquals(1, capturedSlots.size());
        assertEquals("LB1 | 2024-10-22 | 10:00 | 11:00 |", capturedSlots.getFirst()); // Slot between 10:00 and 11:00
    }

    @Test
    void shouldThrowErrorOnInvalidEmailInUserCreation() {
        // Arrange
        String invalidEmail = "invalid.email.com";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new User("Mat123", "John", "Doe", invalidEmail);
        });
        assertEquals("L'email est invalide.", exception.getMessage());
    }

    @Test
    void shouldThrowErrorOnInvalidBookingCapacity() {
        
        Exception exception = assertThrows(BookingException.class, () -> {
            new Booking("Room1", "Mat123", LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(11, 0), "Réunion", -1);
        });
        assertEquals("BookingException -> Nombre de personne ne peut pas être négatif", exception.getMessage());
    }

    @Test
    void shouldThrowErrorOnInvalidRoomCapacity() {
        
        RoomException exception = assertThrows(RoomException.class, () -> {
            new Room("Room1", "Salle A", -1);
        });
        assertEquals("RoomException -> Capacité invalide", exception.getMessage());
    }

    @Test
    void shouldThrowErrorOnInvalidUserDetails() {
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new User("", "Nom", "Prénom", "email@domain.com");
        });
        assertEquals("Le matricule ne peut pas être nul ou vide.", exception.getMessage());
    }
}


