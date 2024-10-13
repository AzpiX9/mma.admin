package org.helmo.mma.admin.infrastructures;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.Email;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.model.property.immutable.ImmutableVersion;
import net.fortuna.ical4j.util.FixedUidGenerator;
import net.fortuna.ical4j.util.UidGenerator;
import org.helmo.mma.admin.domains.booking.CalendarRepository;
import org.helmo.mma.admin.domains.booking.CanReadBooked;
import org.helmo.mma.admin.domains.booking.CanWriteBooked;
import org.helmo.mma.admin.domains.core.Booking;

import java.io.*;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Objet qui permet d'écrire ou de lire un fichier ical (ou ics)
 * @author Adrien Porcu
 */
public class ICALViewer implements CalendarRepository {

    private Calendar calendar;
    private String pathFile;
    private List<VEvent> events = new ArrayList<>();

    public ICALViewer(String path) {
        this.calendar = new Calendar();
        calendar.add(ImmutableVersion.VERSION_2_0);
        this.pathFile = path;
        var outputFile = new File(pathFile,"Event.ics");
        try {
            if(!outputFile.exists()) {
                var fos = new FileOutputStream(outputFile);
                var outputter = new CalendarOutputter();
                outputter.output(calendar, fos);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void writeTo(Booking booking) {
        ZonedDateTime debut = (booking.Debut().atDate(booking.JourReservation())).atZone(ZoneId.systemDefault());
        ZonedDateTime fin = (booking.Fin().atDate(booking.JourReservation())).atZone(ZoneId.systemDefault());

        UidGenerator uidGenerator = null;
        VEvent event = new VEvent(debut,fin,booking.Description());
        Organizer attendee = new Organizer();
        attendee.add(new Cn("Adrien PRC "+booking.Matricule()));
        attendee.add(new Email("a.prc@helmo.be"));
        Location salle = new Location(booking.IdSalle());
        event.add(salle);
        event.add(attendee);

        var outputFile = new File(pathFile,"Event.ics");
        try {
            uidGenerator = new FixedUidGenerator(booking.IdSalle()+"-"+booking.Matricule());
            event.add(uidGenerator.generateUid());
            calendar.add(event);
            var fos = new FileOutputStream(outputFile);
            var outputter = new CalendarOutputter();
            outputter.output(calendar, fos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void readTo() {
        try {
            FileInputStream fis = new FileInputStream(new File(pathFile,"Event.ics"));
            CalendarBuilder builder = new CalendarBuilder();
            Calendar calendarRead = builder.build(fis);

            for(var component : calendarRead.getComponents()) {
                if(component instanceof VEvent event) {
                    events.add(event);
                }
            }
        } catch (IOException | ParserException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<VEvent> retrieveAll() {
        return events;
    }

    @Override
    public Booking getBooking(String id) {
        var reference = events.stream().filter(event -> event.getLocation().get().toString().equals(id)).findFirst().get();
        Booking booking = null;
        //return new Booking(reference.getLocation().get(),);
        return booking;
    }
}
