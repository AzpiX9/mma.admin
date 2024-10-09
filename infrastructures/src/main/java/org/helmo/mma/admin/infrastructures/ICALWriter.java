package org.helmo.mma.admin.infrastructures;

import net.fortuna.ical4j.model.Calendar;
import org.helmo.mma.admin.domains.booking.CanWriteBooked;

/**
 *
 */
public class ICALWriter implements CanWriteBooked  {


    public ICALWriter(){
        Calendar calendar = new Calendar();
    }

    @Override
    public void writeTo() {

    }
}
