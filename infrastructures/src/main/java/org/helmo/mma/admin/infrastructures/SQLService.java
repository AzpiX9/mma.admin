package org.helmo.mma.admin.infrastructures;

import org.helmo.mma.admin.domains.core.Booking;
import org.helmo.mma.admin.domains.core.LocalEvent;
import org.helmo.mma.admin.domains.core.User;
import org.helmo.mma.admin.domains.services.BaseServices;

import java.util.List;
import java.util.Map;

public class SQLService implements BaseServices {


    private final SQLStorage storage;

    public SQLService(SQLStorage storage) {
        this.storage = storage;    
    }

    @Override
    public List<String> getServices() {
        return storage.getSevicesRepository().retrieveAvailableServices();
    }

    @Override
    public Map<String, LocalEvent> getCalendar(){
        return storage.getCalendarRepository().retrieveAll();
    }

    @Override
    public List<String> getServicesFromBooked(String idBooked) {
        return storage.getSevicesRepository().retriveServicesFromBooking(idBooked);
    }

    @Override
    public void addReservationAndServices(Booking booking, User user, List<String> servicesIDs) {

        try {
            storage.beginTransaction();
            storage.getCalendarRepository().writeTo(booking,user);
            var allBooked = storage.getCalendarRepository().retrieveAll();
            String lastId = "";
            for (var booked : allBooked.keySet()){
                lastId = booked;
            }

            storage.getSevicesRepository().insertReservation(lastId,servicesIDs);
            storage.commitTransaction();
        }catch (Exception e) {
            storage.rollbackTransaction();
        }
    }
}
