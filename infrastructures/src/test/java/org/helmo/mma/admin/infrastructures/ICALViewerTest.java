package org.helmo.mma.admin.infrastructures;


import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.Email;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Organizer;
import org.helmo.mma.admin.domains.core.Booking;
import org.helmo.mma.admin.domains.core.LocalEvent;
import org.helmo.mma.admin.domains.core.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ICALViewerTest {

    private static final String TEST_FILE_PATH = "./src/test/resources/Event.ics";
    private ICALViewer icalViewer;

    @BeforeEach
    void setUp() {
        icalViewer = new ICALViewer(TEST_FILE_PATH);
    }

    @AfterEach
    void cleanUp() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_FILE_PATH));
    }

    @Test
    void testWriteToCreatesICalFile() {
        // Création d'un Booking factice
        Booking booking = new Booking("LLL","A123456",LocalDate.now(),LocalTime.of(8,0),LocalTime.of(10,0),"SSS",5);
        User user = new User("A123456","A","B","a.b@de.fg");

        icalViewer.writeTo(booking, user);

        // Vérifier que le fichier a bien été créé
        assertTrue(Files.exists(Paths.get(TEST_FILE_PATH)));
    }

    @Test
    void testWriteToIOException() {
        // Cas d'erreur avec un chemin de fichier incorrect
        ICALViewer invalidViewer = new ICALViewer("/invalid/path.ics");

        Booking booking = new Booking("LLL","A123456",LocalDate.now(),LocalTime.of(8,0),LocalTime.of(10,0),"SSS",5);
        User user = new User("A123456","A","B","a.b@de.fg");

        assertThrows(RuntimeException.class, () -> {
            invalidViewer.writeTo(booking, user);
        });
    }

    @Test
    public void shouldAddEvent(){
        var icsFile = new ICALViewer(TEST_FILE_PATH);
        var bookedRoom = new Booking("A1","X000000", LocalDate.now(),
                LocalTime.of(9,0),LocalTime.of(10,30),
                "Oonga Boonga",13);
        icsFile.writeTo(bookedRoom,new User("X000000","A","B","a.b@cd.ef"));

        assertTrue(!icsFile.retrieveAll().isEmpty());
    }

    @Test void shouldRead_AndRetrieveFirstVEvent(){
        String pathName = "./src/test/resources/Event.ics";
        var icsFile = new ICALViewer(pathName);

        var bookedRoom = new Booking("A1","X000000", LocalDate.now(),
                LocalTime.of(9,0),LocalTime.of(10,30),
                "Oonga Boonga",13);
        icsFile.writeTo(bookedRoom,new User("X000000","A","B","a.b@cd.ef"));

        var all = icsFile.retrieveAll();
        LocalEvent expected = new LocalEvent("A_B_X000000_a.b@cd.ef","A1",LocalDate.now(),LocalTime.of(9,0),LocalTime.of(10,30),"Oonga Boonga");
        LocalEvent actualEvent = all.get(0);
        assertEquals(expected,actualEvent);
    }

    @Test
    void shouldWrite_AndRetrieveVEvent(){
        List<VEvent> events = new ArrayList<>();

        var org1 = new Organizer().add(new Cn("Jean Mi")).add(new Email("j.mi@helmo.be"));
        var loc1 = new Location("LB1");
        var ev1 = createVEvent(
                ZonedDateTime.of(LocalDate.now(),LocalTime.of(10,0), ZoneId.systemDefault()),
                ZonedDateTime.of(LocalDate.now(),LocalTime.of(12,30), ZoneId.systemDefault())
                ,"EEEE",loc1,(Organizer) org1);

        var org2 = new Organizer().add(new Cn("Jonny Géchar")).add(new Email("j.gechar@helmo.be"));
        var loc2 = new Location("LB2");
        var ev2 = createVEvent(ZonedDateTime.now().plusDays(1),ZonedDateTime.now().plusDays(1).plusMinutes(30),"FFFF",loc2,(Organizer) org2);

        var org3 = new Organizer().add(new Cn("Jean Mi")).add(new Email("j.mi@helmo.be"));
        var loc3 = new Location("LB1");
        var ev3 = createVEvent(
                ZonedDateTime.of(LocalDate.now(),LocalTime.of(14,0), ZoneId.systemDefault()),
                ZonedDateTime.of(LocalDate.now(),LocalTime.of(15,30), ZoneId.systemDefault()),
                "EEEE2",loc3,(Organizer) org3);

        events.add(ev1);
        events.add(ev2);
        events.add(ev3);

        List<String> allDay = new ArrayList<>();
        List<String> times = new ArrayList<>();

        for (var e : events){
            var tempB = LocalTime.from( e.getDateTimeStart().get().getDate());
            var tempE = LocalTime.from( e.getEndDate().get().getDate());
            if(LocalDate.from(e.getDateTimeStart().get().getDate()).equals(LocalDate.now())){
                times.add(tempB+"-"+tempE);
            }
        }
        times.forEach(System.out::println);

        LocalTime lt = LocalTime.of(8,0);
        for (int i = 0; i < 18; i++) {
            String result = "0";
            for (var t : times){
                var bounds = t.split("-");
                var begin = LocalTime.parse(bounds[0]);
                var end = LocalTime.parse(bounds[1]);
                if ((lt.isAfter(begin) || lt.equals(begin)) && (lt.isBefore(end))) {
                    result = "\u274c";
                    break;
                }
                allDay.add(result);
            }
            System.out.print(result+" ");
            lt = lt.plusMinutes(30);
        }

        allDay.forEach(System.out::print);

    }

    @Test
    public void shouldRetrieve_EventsOnSpecified_Location_And_Time(){
        String pathName = "./src/test/resources/Event2.ics";
        var icsFile = new ICALViewer(pathName);

        List<LocalEvent> allEvs = new ArrayList<>();
        allEvs.add(new LocalEvent("AAA","LLL",LocalDate.of(2024,10,16),LocalTime.of(9,0), LocalTime.of(10,30),"SSS" ));
        allEvs.add(new LocalEvent("BBB","VVV",LocalDate.of(2024,10,16),LocalTime.of(10,0), LocalTime.of(10,30),"SSS" ));
        allEvs.add(new LocalEvent("AAA","LLL",LocalDate.of(2024,10,16),LocalTime.of(12,0), LocalTime.of(13,30),"SSS" ));

        var expected = allEvs.stream().filter(s -> s.Location().equals("LLL") && LocalDate.of(2024,10,16).equals(s.DateJour())).toList();
        var actual = icsFile.getBookingsBy("LLL",LocalDate.of(2024,10,16));

        assertEquals(expected,actual);

    }

    @Test
    void testReadToLoadsEvents() {
        // Écrire un événement pour que le fichier ne soit pas vide
        Booking booking = new Booking("LLL","A123456",LocalDate.now(),LocalTime.of(8,0),LocalTime.of(10,0),"SSS",5);
        User user = new User("A123456","A","B","a.b@de.fg");

        icalViewer.writeTo(booking, user);

        icalViewer.readTo();
        List<LocalEvent> events = icalViewer.retrieveAll();

        // Vérifier que l'événement a bien été ajouté
        assertEquals(1, events.size());
    }

    @Test
    void testReadToParserException() {
        // Simuler un fichier iCal corrompu
        Path invalidFile = Paths.get(TEST_FILE_PATH);
        try {
            Files.writeString(invalidFile, "INVALID_ICAL_CONTENT");
        } catch (IOException e) {
            fail("Erreur d'écriture de fichier de test.");
        }

        assertThrows(RuntimeException.class, () -> icalViewer.readTo());
    }

    @Test
    void testGetBookingReturnsCorrectEvent() {
        // Écrire un événement pour tester
        Booking booking = new Booking("LLL","A123456",LocalDate.now(),LocalTime.of(8,0),LocalTime.of(10,0),"SSS",5);
        User user = new User("A123456","A","B","a.b@de.fg");

        icalViewer.writeTo(booking, user);

        // Rechercher l'événement à l'heure correspondante
        LocalEvent event = icalViewer.getBooking(booking.IdSalle(), booking.Debut());

        assertNotNull(event);
        assertEquals(booking.IdSalle(), event.Location());
    }

    @Test
    void testGetBookingsByLocationAndDate() {
        // Écrire un événement pour une salle et une date
        Booking booking = new Booking("LLL","A123456",LocalDate.now(),LocalTime.of(8,0),LocalTime.of(10,0),"SSS",5);
        User user = new User("A123456","A","B","a.b@de.fg");

        icalViewer.writeTo(booking, user);

        List<LocalEvent> events = icalViewer.getBookingsBy(booking.IdSalle(), booking.JourReservation());

        assertFalse(events.isEmpty());
        assertEquals(booking.IdSalle(), events.get(0).Location());
    }
    @Test
    void testGetBookingWithTimeFilters() {
        // Écrire des événements factices pour tester
        Booking booking1 = new Booking("LLL", "A123456",LocalDate.now(),LocalTime.of(10, 0), LocalTime.of(12, 0), "Salle1", 8);
        Booking booking2 = new Booking("LLL", "A123456",LocalDate.now(),LocalTime.of(13, 0), LocalTime.of(14, 0), "Salle1", 7);
        User user = new User("A123456","A","B","a.b@de.fg");

        icalViewer.writeTo(booking1, user);
        icalViewer.writeTo(booking2, user);

        // Rechercher un événement entre 10h et 12h
        //LocalEvent event = icalViewer.getBooking("Salle1", LocalTime.of(11, 0));
        //assertNotNull(event);
        //assertEquals("Salle1", event.Location());

        // Rechercher un événement en dehors de ces heures
        LocalEvent noEvent = icalViewer.getBooking("Salle1", LocalTime.of(9, 0));
        assertNull(noEvent);
    }

    private VEvent createVEvent(ZonedDateTime beginT, ZonedDateTime endT, String summary, Location location, Organizer organizer) {
        var event = new VEvent(beginT,endT,summary);
        event.add(location);
        event.add(organizer);
        return event;
    }


}