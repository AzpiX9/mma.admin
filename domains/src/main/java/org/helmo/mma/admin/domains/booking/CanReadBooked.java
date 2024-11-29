package org.helmo.mma.admin.domains.booking;

import org.helmo.mma.admin.domains.core.LocalEvent;

import java.util.Map;

public interface CanReadBooked {
    void readTo();

    Map<String,LocalEvent> retrieveAll(); //TODO: ajouter une date


}
