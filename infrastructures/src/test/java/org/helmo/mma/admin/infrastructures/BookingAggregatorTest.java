package org.helmo.mma.admin.infrastructures;

import org.helmo.mma.admin.domains.booking.CalendarRepository;
import org.helmo.mma.admin.domains.rooms.CanReadRooms;
import org.helmo.mma.admin.domains.services.BaseServices;
import org.helmo.mma.admin.domains.users.CanReadUsers;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

public class BookingAggregatorTest {
    @Test
    void shouldCreateBookingAggregatorWithValidDependencies() {
        // Arrange
        CanReadRooms mockRoomRepo = mock(CanReadRooms.class);
        CanReadUsers mockUserRepo = mock(CanReadUsers.class);
        CalendarRepository mockCalendarRepo = mock(CalendarRepository.class);
        BaseServices mockService = mock(BaseServices.class);

        // Act
        BookingAggregator aggregator = new BookingAggregator(mockRoomRepo, mockUserRepo, mockCalendarRepo, mockService);

        // Assert
        assertNotNull(aggregator.getRoomsRepo());
        assertNotNull(aggregator.getUsersRepo());
        assertNotNull(aggregator.getCalendarRepository());
        assertNotNull(aggregator.getAService());
    }

}