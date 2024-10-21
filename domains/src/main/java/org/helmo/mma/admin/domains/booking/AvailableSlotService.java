package org.helmo.mma.admin.domains.booking;

import org.helmo.mma.admin.domains.core.LocalEvent;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AvailableSlotService {

    private static final LocalTime BEGIN = LocalTime.of(8, 0);
    private static final LocalTime END = LocalTime.of(17, 0);

    private final List<LocalEvent> availableSlots;



    public AvailableSlotService(List<LocalEvent> eventsGiven){
        availableSlots = eventsGiven != null ? List.copyOf(eventsGiven) : new ArrayList<>();
    }

    private LocalTime[] checksAvailableSlots(){
        LocalTime longestStart = null, longestEnd = null;
        LocalTime currentStart = null;
        LocalTime temp = BEGIN;
        int longestDuration = 0, currentDuration = 0;

        while (!temp.isAfter(END)) {
            LocalTime finalTemp = temp;
            boolean isAvailable = availableSlots.stream()
                    .noneMatch(interval -> !finalTemp.isBefore(interval.Debut()) && finalTemp.isBefore(interval.Fin()));

            if (isAvailable) {
                if (currentStart == null) {
                    currentStart = temp;
                }
                currentDuration += 30;
            } else {
                // Si la séquence est interrompue, vérifier si elle est la plus longue
                if (currentDuration > longestDuration) {
                    longestStart = currentStart;
                    longestEnd = temp;
                    longestDuration = currentDuration;
                }
                // Réinitialiser les variables de la séquence
                currentStart = null;
                currentDuration = 0;
            }

            temp = temp.plusMinutes(30);  // Incrémentation par 30 minutes
        }

        if (currentDuration > longestDuration) {
            longestStart = currentStart;
            longestEnd = END;
        }

        return new LocalTime[] {longestStart,longestEnd};

    }

    public String computeTimeSlot() {
        var result = checksAvailableSlots();
        return result[0].format(DateTimeFormatter.ofPattern("H:mm"))+"-"+result[1].format(DateTimeFormatter.ofPattern("H:mm"));
    }

    public LocalTime getTimeDifference(){
        var result = checksAvailableSlots();

        Duration duration = Duration.between(result[0], result[1]);
        var hours = (int) duration.toHours();
        var minutes = (int) (duration.toMinutes() % 60);

        return LocalTime.of(hours,minutes);
    }



}
