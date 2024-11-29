package org.helmo.mma.admin.domains.booking;

import org.helmo.mma.admin.domains.core.LocalEvent;

import java.util.ArrayList;
import java.util.List;

public class EventUtils {

    public static List<String> transform(List<LocalEvent> evs){
        List<String> times = new ArrayList<>();
        for (LocalEvent event : evs) {
            var tempStr = event.Debut()+"-"+event.Fin();
            times.add(tempStr);
        }

        return times;
    }

}
