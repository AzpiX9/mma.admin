/*
 * This source file is an example
 */
package org.helmo.mma.admin.presentations;

import org.helmo.mma.admin.domains.core.BookingAggregator;
import org.helmo.mma.admin.domains.rooms.CanReadRooms;

import java.time.LocalDate;

public class MainPresenter implements BookingPresenter {

    private MainView view;
    private BookingAggregator aggregator;

    public MainPresenter(BookingAggregator aggregator) {
        this.aggregator = aggregator;
    }

    @Override
    public void setView(MainView view) {
        this.view = view;

    }

    @Override
    public void seeBookingRequest(LocalDate date) {
        CanReadRooms canReadRooms = aggregator.getRoomsRepo();
        for (var room : canReadRooms.getRooms()) {
            System.out.println(room.Id());
        }
    }
}
