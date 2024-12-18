package org.helmo.mma.admin.presentations;

import org.helmo.mma.admin.domains.booking.CalendarRepository;
import org.helmo.mma.admin.domains.core.*;
import org.helmo.mma.admin.domains.exceptions.BookingException;
import org.helmo.mma.admin.domains.exceptions.RoomException;
import org.helmo.mma.admin.domains.exceptions.UserException;
import org.helmo.mma.admin.domains.rooms.CanReadRooms;
import org.helmo.mma.admin.domains.services.BaseServices;
import org.helmo.mma.admin.domains.users.CanReadUsers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class MainPresenterTest {

    private static final List<User> USERS_DATA = List.of(
            new User("A123456","Jean","Dupont","j.dupont@helmo.be"),
            new User("B234567","Marie","Dubois","m.dubois@helmo.be")
    );

    private static final List<Room> ROOMS_DATA;

    static {
        try {
            ROOMS_DATA = List.of(
                    new Room("S111","Salle 11",20),
                    new Room("S122","Salle 22",20)
            );
        } catch (RoomException e) {
            throw new RuntimeException(e);
        }
    }


    private MainView view;
    private BaseAggregator aggregator;
    private BaseServices reservationService;
    private UserManager userManager;
    private RoomManager roomManager;
    private BookingManager bookingManager;
    private MainPresenter presenter;

    @BeforeEach
    public void setUp() throws UserException {
        view = mock(MainView.class);
        aggregator = mock(BaseAggregator.class);
        reservationService = mock(BaseServices.class);
        userManager = mock(UserManager.class);
        roomManager = mock(RoomManager.class);
        bookingManager = mock(BookingManager.class);

        // Mocking UsersRepo
        CanReadUsers usersRepo = mock(CanReadUsers.class);
        when(aggregator.getUsersRepo()).thenReturn(usersRepo);
        when(usersRepo.getUsers()).thenReturn(USERS_DATA);

        // Mocking RoomsRepo
        CanReadRooms roomsRepo = mock(CanReadRooms.class);
        when(aggregator.getRoomsRepo()).thenReturn(roomsRepo);
        when(roomsRepo.getRooms()).thenReturn(ROOMS_DATA);

        // Mocking CalendarRepository
        CalendarRepository calendarRepository = mock(CalendarRepository.class);
        when(aggregator.getCalendarRepository()).thenReturn(calendarRepository);
        when(calendarRepository.retrieveAll()).thenReturn(Map.of());

        when(aggregator.getAService()).thenReturn(reservationService);
        presenter = new MainPresenter(view, aggregator);
    }

    @Test
    public void testSeeBookingRequest() {
        // Arrange
        LocalDate date = LocalDate.now();

        List<LocalEvent> eventsRoom1 = List.of(
                new LocalEvent("A123456", "S111", date, LocalTime.of(8, 0), LocalTime.of(9, 0), "Event 1"),
                new LocalEvent("B234567", "S111", date, LocalTime.of(10, 0), LocalTime.of(11, 0), "Event 2")
        );
        List<LocalEvent> eventsRoom2 = List.of(
                new LocalEvent("C345678", "S122", date, LocalTime.of(11, 30), LocalTime.of(13, 0), "Event 3")
        );

        when(roomManager.getRooms()).thenReturn(ROOMS_DATA);
        when(bookingManager.getBookingsBy("S111", date)).thenReturn(eventsRoom1);
        when(bookingManager.getBookingsBy("S122", date)).thenReturn(eventsRoom2);

        // Act
        presenter.seeBookingRequest(date);

        // Assert
        ArgumentCaptor<String> roomCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List<String>> eventsCaptor = ArgumentCaptor.forClass(List.class);

        verify(view, times(2)).displayByLocalEvs(roomCaptor.capture(), eventsCaptor.capture());

        List<String> capturedRooms = roomCaptor.getAllValues();
        List<List<String>> capturedEvents = eventsCaptor.getAllValues();

        // Assertions for Room 1
        assertEquals("S111", capturedRooms.get(0));
        assertEquals(List.of("08:00-09:00", "10:00-11:00"), capturedEvents.get(0));

        // Assertions for Room 2
        assertEquals("S122", capturedRooms.get(1));
        assertEquals(List.of("11:30-13:00"), capturedEvents.get(1));
    }





    @Test
    public void testWriteEventRequest_Failure() {
        // Arrange
        String request = "S111, Meeting, "+LocalDate.now()+", 08:00, 10:00, Summary, 12345";

        ArgumentCaptor<String> errorCaptor = ArgumentCaptor.forClass(String.class);

        // Act
        presenter.writeEventRequest(request);

        // Assert
        verify(view, times(1)).displayError(errorCaptor.capture());
        assertEquals("Capacité insuffisante", errorCaptor.getValue());
    }




    @Test
    public void testAvailableRequest() throws RoomException {
        // Arrange
        String request = "2024-12-01, 10, 2:00";
        List<Room> rooms = List.of(new Room("1", "Room A", 10));
        List<String> availableRooms = List.of("Room A: 08:00-10:00");

        when(roomManager.getRoomsByMaxSize(10)).thenReturn(rooms);
        when(bookingManager.checkAvailableOnAll(rooms, LocalDate.parse("2024-12-01"), "2:00")).thenReturn(availableRooms);

        // Act
        presenter.availableRequest(request);

        // Assert
        verify(view, times(1)).displayAvailable(eq(availableRooms));
    }
}


