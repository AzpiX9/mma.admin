package org.helmo.mma.admin.infrastructures;

import static org.junit.jupiter.api.Assertions.*;

import org.helmo.mma.admin.domains.booking.CalendarRepository;
import org.helmo.mma.admin.domains.core.Booking;
import org.helmo.mma.admin.domains.core.LocalEvent;
import org.helmo.mma.admin.domains.core.User;
import org.helmo.mma.admin.domains.services.SevicesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SQLServiceTest {
    @Mock
    private SQLStorage mockStorage;

    @Mock
    private SevicesRepository mockServicesRepo;

    @Mock
    private CalendarRepository mockCalendarRepo;

    private SQLService sqlService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(mockStorage.getSevicesRepository()).thenReturn(mockServicesRepo);
        when(mockStorage.getCalendarRepository()).thenReturn(mockCalendarRepo);

        sqlService = new SQLService(mockStorage);
    }

    @Test
    void testGetServices() {
        // Arrange
        List<String> expectedServices = List.of("Projector", "Catering", "Audio");
        when(mockServicesRepo.retrieveAvailableServices()).thenReturn(expectedServices);

        // Act
        List<String> actualServices = sqlService.getServices();

        // Assert
        assertEquals(expectedServices, actualServices);
        verify(mockServicesRepo, times(1)).retrieveAvailableServices();
    }

    @Test
    void testGetCalendar() {
        // Arrange
        Map<String, LocalEvent> expectedCalendar = Map.of(
                "1", new LocalEvent("User1", "Room1", LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0), "Meeting")
        );
        when(mockCalendarRepo.retrieveAll()).thenReturn(expectedCalendar);

        // Act
        Map<String, LocalEvent> actualCalendar = sqlService.getCalendar();

        // Assert
        assertEquals(expectedCalendar, actualCalendar);
        verify(mockCalendarRepo, times(1)).retrieveAll();
    }

    @Test
    void testGetServicesFromBooked() {
        // Arrange
        String bookingId = "123";
        List<String> expectedServices = List.of("Projector", "Catering");
        when(mockServicesRepo.retriveServicesFromBooking(bookingId)).thenReturn(expectedServices);

        // Act
        List<String> actualServices = sqlService.getServicesFromBooked(bookingId);

        // Assert
        assertEquals(expectedServices, actualServices);
        verify(mockServicesRepo, times(1)).retriveServicesFromBooking(bookingId);
    }

    @Test
    void testAddReservationAndServices_Success() {
        // Arrange
        Booking booking = new Booking("Room1", "User1", LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0), "Meeting", 5);
        User user = new User("123", "Doe", "John", "john.doe@example.com");
        List<String> servicesIDs = List.of("1", "2", "3");
        Map<String, LocalEvent> allBooked = Map.of("123", new LocalEvent("User1", "Room1", LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0), "Meeting"));

        when(mockCalendarRepo.retrieveAll()).thenReturn(allBooked);

        // Act
        assertDoesNotThrow(() -> sqlService.addReservationAndServices(booking, user, servicesIDs));

        // Assert
        verify(mockStorage, times(1)).beginTransaction();
        verify(mockCalendarRepo, times(1)).writeTo(booking, user);
        verify(mockServicesRepo, times(1)).insertReservation("123", servicesIDs);
        verify(mockStorage, times(1)).commitTransaction();
    }

    @Test
    void testAddReservationAndServices_Failure_Rollback() {
        // Arrange
        Booking booking = new Booking("Room1", "User1", LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0), "Meeting", 5);
        User user = new User("123", "Doe", "John", "john.doe@example.com");
        List<String> servicesIDs = List.of("1", "2", "3");

        doThrow(new RuntimeException("Database error")).when(mockCalendarRepo).writeTo(booking, user);

        // Act
        assertDoesNotThrow(() -> sqlService.addReservationAndServices(booking, user, servicesIDs));

        // Assert
        verify(mockStorage, times(1)).beginTransaction();
        verify(mockCalendarRepo, times(1)).writeTo(booking, user);
        verify(mockStorage, times(1)).rollbackTransaction();
        verify(mockStorage, never()).commitTransaction();
    }
}