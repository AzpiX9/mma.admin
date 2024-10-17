package org.helmo.mma.admin.infrastructures;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.model.property.immutable.ImmutableVersion;
import net.fortuna.ical4j.util.FixedUidGenerator;
import net.fortuna.ical4j.util.UidGenerator;
import org.helmo.mma.admin.domains.booking.CalendarRepository;
import org.helmo.mma.admin.domains.core.Booking;
import org.helmo.mma.admin.domains.core.LocalEvent;
import org.helmo.mma.admin.domains.core.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Objet qui permet d'interagir vers un fichier ical (ou ics)
 * @author Adrien Porcu
 */
public class ICALViewer implements CalendarRepository {

    private Calendar calendar;
    private String pathFile;
    private List<LocalEvent> events = new ArrayList<>();

    public ICALViewer(String path) {
        this.calendar = new Calendar();
        this.calendar.add(ImmutableVersion.VERSION_2_0);
        this.pathFile = path;

    }

    @Override
    public void writeTo(Booking booking, User user) {
        ZonedDateTime debut = (booking.Debut().atDate(booking.JourReservation())).atZone(ZoneId.systemDefault());
        ZonedDateTime fin = (booking.Fin().atDate(booking.JourReservation())).atZone(ZoneId.systemDefault());

        VEvent event = parseVEvent(booking, user, debut, fin);
        UidGenerator uidGenerator;
        var outputFile = new File(pathFile);
        try(var fos = Files.newOutputStream(Paths.get(pathFile))) {
            uidGenerator = new FixedUidGenerator(booking.IdSalle()+"-"+booking.Matricule());
            event.add(uidGenerator.generateUid());
            calendar.add(event);
            var outputter = new CalendarOutputter();
            outputter.output(calendar, fos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static VEvent parseVEvent(Booking booking, User user, ZonedDateTime debut, ZonedDateTime fin) {
        VEvent event = new VEvent(debut, fin, booking.Description());
        Organizer attendee = new Organizer(user.Nom()+"_"+ user.Prenom()+"_"+ booking.Matricule()+"_"+user.Email());
        Location salle = new Location(booking.IdSalle());
        event.add(salle);
        event.add(attendee);

        return event;
    }

    @Override
    public void readTo() {
        events.clear();
        try(var fis = Files.newInputStream(Paths.get(pathFile))) {
            CalendarBuilder builder = new CalendarBuilder();
            Calendar calendarRead = builder.build(fis);

            for(var component : calendarRead.getComponents()) {
                if(component instanceof VEvent event) {
                    var localEvent = parseToBooking(event);
                    events.add(localEvent);
                }
            }
        } catch (IOException | ParserException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<LocalEvent> retrieveAll() {
        readTo();
        return events;
    }

    @Override
    public LocalEvent getBooking(String id, LocalTime givenTime) {
        var all = retrieveAll();
        LocalEvent result = null;
        for(var event : all) {
            if(event.Location().equals(id) && isBetweenTime(event,givenTime)) {
                result = event;
            }
        }
        return result;
    }

    @Override
    public List<LocalEvent> getBookingsBy(String location, LocalDate date) {
        return retrieveAll()
                .stream()
                .filter(s -> s.Location().equals(location) && date.equals(s.DateJour()))
                .toList();
    }

    private boolean isBetweenTime(LocalEvent event, LocalTime crenau) {
        return (crenau.equals(event.Debut()) || crenau.equals(event.Fin())) ||
                (crenau.isAfter(event.Debut()) && crenau.isBefore(event.Fin()));
    }

    public LocalEvent parseToBooking(VEvent vEvent){
        var loc = vEvent.getLocation().get().getValue();
        var org = vEvent.getOrganizer().get().getValue();
        LocalDate day = LocalDate.from(vEvent.getDateTimeStart().get().getDate());
        LocalTime startT = LocalTime.from(vEvent.getDateTimeStart().get().getDate());
        LocalTime endT = LocalTime.from(vEvent.getDateTimeEnd().get().getDate());
        var summary = vEvent.getSummary().get().getValue();

        return new LocalEvent(org,loc,day,startT,endT,summary);
    }
}
