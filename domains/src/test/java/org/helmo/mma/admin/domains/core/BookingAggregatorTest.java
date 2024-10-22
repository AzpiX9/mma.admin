package org.helmo.mma.admin.domains.core;

import org.helmo.mma.admin.domains.booking.CalendarRepository;
import org.helmo.mma.admin.domains.rooms.CanReadRooms;
import org.helmo.mma.admin.domains.users.CanReadUsers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class BookingAggregatorTest {
    @Mock
    private CanReadRooms mockRoomsRepo;

    @Mock
    private CanReadUsers mockUsersRepo;

    @Mock
    private CalendarRepository mockCalendarRepo;

    private BookingAggregator aggregator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConstructorShouldNotAcceptNullDependencies() {
        assertThrows(NullPointerException.class, () -> {
            new BookingAggregator(mockRoomsRepo, mockUsersRepo, null);
        });

        assertThrows(NullPointerException.class, () -> {
            new BookingAggregator(mockRoomsRepo, null, mockCalendarRepo);
        });

        assertThrows(NullPointerException.class, () -> {
            new BookingAggregator(null, mockUsersRepo, mockCalendarRepo);
        });
    }

    @Test
    void testConstructorShouldInitializeDependencies() {
        aggregator = new BookingAggregator(mockRoomsRepo, mockUsersRepo, mockCalendarRepo);

        assertNotNull(aggregator.getRoomsRepo());
        assertNotNull(aggregator.getUsersRepo());
        assertNotNull(aggregator.getCalendarRepository());

        assertSame(mockRoomsRepo, aggregator.getRoomsRepo());
        assertSame(mockUsersRepo, aggregator.getUsersRepo());
        assertSame(mockCalendarRepo, aggregator.getCalendarRepository());
    }
}