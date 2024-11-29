package org.helmo.mma.admin.presentations;

import org.helmo.mma.admin.domains.booking.CalendarRepository;
import org.helmo.mma.admin.domains.core.*;
import org.helmo.mma.admin.domains.rooms.CanReadRooms;
import org.helmo.mma.admin.domains.services.BaseServices;
import org.helmo.mma.admin.domains.users.CanReadUsers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MainPresenterTest {
    private MainView view;
    private BaseAggregator aggregator;
    private CanReadUsers usersRepo;
    private CanReadRooms roomsRepo;
    private CalendarRepository calendarRepository;
    private BaseServices reservationService;

    private BookingManager bookingManager;
    private UserManager userManager;
    private RoomManager roomManager;

    private BookingPresenter presenter;

    @BeforeEach
    void setUp() {
        // Mock des dépendances
        view = mock(MainView.class);
        aggregator = mock(BaseAggregator.class);
        usersRepo = mock(CanReadUsers.class);
        roomsRepo = mock(CanReadRooms.class);
        calendarRepository = mock(CalendarRepository.class);
        reservationService = mock(BaseServices.class);

        // Simulations pour le BaseAggregator
        when(aggregator.getUsersRepo()).thenReturn(usersRepo);
        when(aggregator.getRoomsRepo()).thenReturn(roomsRepo);
        when(aggregator.getCalendarRepository()).thenReturn(calendarRepository);
        when(aggregator.getAService()).thenReturn(reservationService);


        // Création de l'instance MainPresenter
        presenter = new MainPresenter(view, aggregator);
    }

    @Test
    public void shouldDisplayBookingRequestsForGivenDate() {
        when(usersRepo.getUsers()).thenReturn(List.of(new User("MA123","John","Doe","j.doe@helmo.be")));
        when(roomsRepo.getRooms()).thenReturn(List.of(new Room("SA1", "Salle A", 10)));
        when(calendarRepository.retrieveAll()).thenReturn(Map.of("A",new LocalEvent("MA123", "SA1", LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(11, 0), "Réunion")));
        bookingManager = new BookingManager(calendarRepository.retrieveAll());
        var events = bookingManager.eventsToString(LocalDate.now(),"SA1");

        // Appelez la méthode à tester
        presenter.seeBookingRequest(LocalDate.now());

        // Vérifiez que la méthode a été appelée avec les bons arguments
        verify(view).displayByLocalEvs(eq("SA1"), eq(events));
    }

    @Test
    void shouldHandleMultipleAvailableSlotsCorrectly() {
        // Arrange
        String request = "2024-10-22, 5, 01:00";
        Room room = new Room("Room1", "Salle A", 10);
        List<Room> rooms = List.of(room);
        LocalDate date = LocalDate.parse("2024-10-22");


        when(roomsRepo.getRooms()).thenReturn(rooms);
        when(calendarRepository.retrieveAll()).thenReturn(Map.of(
                "A1",new LocalEvent("A123456", "LB1", date, LocalTime.of(9, 0), LocalTime.of(10, 0), "Réunion"),
                "A2",new LocalEvent("A123456", "LB1", date, LocalTime.of(11, 0), LocalTime.of(12, 0), "Réunion")
        ));

        // Act
        presenter.availableRequest(request);

        // Assert
        ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);
        verify(view).displayAvailable(captor.capture());

        List<String> capturedSlots = captor.getValue();
        assertEquals("LB1 | 2024-10-22 | 10:00 | 11:00 |", capturedSlots.getFirst()); // Slot between 10:00 and 11:00
    }

    @Test
    void shouldDisplayNoAvailableSlotsWhenNoneFound() {
        // Données d'entrée
        String request = "2024-12-01,15,02:00";

        // Simulations
        when(roomsRepo.getRooms()).thenReturn(List.of());
        when(bookingManager.checkAvailableOnAll(anyList(), eq(LocalDate.of(2024, 12, 1)), eq("02:00")))
                .thenReturn(List.of());

        // Appel de la méthode
        presenter.availableRequest(request);

        // Vérification
        verify(view).displayAvailable(List.of());
    }


}


