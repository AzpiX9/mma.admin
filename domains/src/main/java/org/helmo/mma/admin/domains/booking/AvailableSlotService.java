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

    private LocalTime[] checksAvailableSlots() {
        LocalTime[] currentSlot = {null, null};
        LocalTime[] longestSlot = {null, null};
        int longestDuration = 0;

        LocalTime temp = BEGIN;
        while (!temp.isAfter(END)) {
            if (isAvailableAt(temp)) {
                currentSlot = updateCurrentSlot(currentSlot, temp);
            } else {
                longestDuration = updateLongestIfNeeded(currentSlot, longestSlot, longestDuration, temp);
                resetCurrentSlot(currentSlot);
            }
            temp = temp.plusMinutes(30);
        }
        updateLongestIfEnd(currentSlot, longestSlot, longestDuration);

        return longestSlot;
    }

    private boolean isAvailableAt(LocalTime time) {
        return availableSlots.stream()
                .noneMatch(interval -> !time.isBefore(interval.Debut()) && time.isBefore(interval.Fin()));
    }

    private LocalTime[] updateCurrentSlot(LocalTime[] currentSlot, LocalTime time) {
        if (currentSlot[0] == null) {
            currentSlot[0] = time;
        }
        currentSlot[1] = time;
        return currentSlot;
    }

    private int updateLongestIfNeeded(LocalTime[] currentSlot, LocalTime[] longestSlot, int longestDuration, LocalTime end) {
        int currentDuration = calculateDuration(currentSlot[0], end);
        if (currentDuration > longestDuration) {
            longestSlot[0] = currentSlot[0];
            longestSlot[1] = end;
            return currentDuration;
        }
        return longestDuration;
    }

    private void resetCurrentSlot(LocalTime[] currentSlot) {
        currentSlot[0] = null;
        currentSlot[1] = null;
    }

    private void updateLongestIfEnd(LocalTime[] currentSlot, LocalTime[] longestSlot, int longestDuration) {
        int currentDuration = calculateDuration(currentSlot[0], END);
        if (currentDuration > longestDuration) {
            longestSlot[0] = currentSlot[0];
            longestSlot[1] = END;
        }
    }

    private int calculateDuration(LocalTime start, LocalTime end) {
        return (start != null && end != null) ? (int) java.time.Duration.between(start, end).toMinutes() : 0;
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
