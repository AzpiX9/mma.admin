/*
 * This source file is an example
 */
package org.helmo.mma.admin.presentations;

import org.helmo.mma.admin.domains.rooms.CanReadRooms;

import java.time.LocalDate;

public class MainPresenter implements BookingPresenter {

    private MainView view;
    private CanReadRooms canReadRooms;

    public MainPresenter(CanReadRooms roomRepository) {
        canReadRooms = roomRepository;
    }

    @Override
    public void setView(MainView view) {
        this.view = view;

    }

    @Override
    public void seeBookingRequest(LocalDate date) {
        for (var room : canReadRooms.getRooms()) {
            System.out.println(room.Id());
        }
    }
}
