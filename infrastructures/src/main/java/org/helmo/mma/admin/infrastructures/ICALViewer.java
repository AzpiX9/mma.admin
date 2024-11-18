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
import org.helmo.mma.admin.domains.exceptions.CalendarException;

import java.io.*;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.*;
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
        var directory = Paths.get(path);
        this.pathFile = path;
        try (var inputS = Files.newInputStream(directory)){
            var builder = new CalendarBuilder();
            this.calendar = builder.build(inputS);
            this.calendar.add(ImmutableVersion.VERSION_2_0);
        } catch (IOException | ParserException e) {
            throw new CalendarException(e.getMessage());
        }

    }

    @Override
    public void writeTo(Booking booking, User user) {
        var bookingDto = new BookingDTO(booking);

        if(booking.JourReservation().isBefore(LocalDate.now())) {
            return; //On sort de la méthode car on veut écrire à une date antérieur
        }

        try(var fos = Files.newOutputStream(Paths.get(pathFile))) {
            VEvent event = parseVEvent(bookingDto, user, bookingDto.getDebut(), bookingDto.getFin());
            calendar.add(event);
            var outputter = new CalendarOutputter();
            outputter.output(calendar, fos);
            events.add(parseToBooking(event));
        } catch (IOException e) {
            throw new CalendarException("Calendrier invalide");
        }

    }

    private static VEvent parseVEvent(BookingDTO booking, User user, ZonedDateTime debut, ZonedDateTime fin) throws SocketException {
        VEvent event = new VEvent(debut, fin, booking.getDescription());
        Organizer attendee = new Organizer(user.Nom()+"_"+ user.Prenom()+"_"+ booking.getMatricule()+"_"+user.Email());
        Location salle = new Location(booking.getSalle());
        Summary summary = new Summary(booking.getDescription());
        UidGenerator uidGenerator = new FixedUidGenerator(booking.getSalle()+"-"+booking.getMatricule());
        event.add(uidGenerator.generateUid());
        event.add(salle);
        event.add(attendee);
        event.add(summary);
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
        }catch (ParserException | IOException e) {
            throw new CalendarException("Les composants sont invalides");
        }

    }

    @Override
    public List<LocalEvent> retrieveAll() {
        readTo();
        return events;
    }

    /**
     * @param id
     * @param givenTime
     * @return
     */
    @Override
    public LocalEvent getBooking(String id, LocalDateTime givenTime) {
        LocalEvent result = null;

        for(var event : retrieveAll()) {
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

    private boolean isBetweenTime(LocalEvent event, LocalDateTime crenau) {

        var eventReferenceStart = LocalDateTime.of(event.DateJour(),event.Debut());
        var eventReferenceEnd = LocalDateTime.of(event.DateJour(),event.Fin());
        return (crenau.equals(eventReferenceStart))
                || (crenau.isAfter(eventReferenceStart)
                && crenau.isBefore(eventReferenceEnd));
    }

    /**
     * Analyse et transforme un objet {@code VEvent} en {@code LocalEvent}
     * @param vEvent
     * @return LocalEvent
     */
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
