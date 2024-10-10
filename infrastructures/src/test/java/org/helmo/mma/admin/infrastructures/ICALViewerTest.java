package org.helmo.mma.admin.infrastructures;

import org.helmo.mma.admin.domains.core.Booking;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class ICALViewerTest {

    @Test
    public void outputFile_shouldExist(){
        String pathName = "./src/test/resources/";
        var icsFile = new ICALViewer(pathName);

        File actual = new File(pathName,"Event.ics");
        assertTrue(actual.exists());
    }
    @Test
    public void shouldAddEvent(){
        String pathName = "./src/test/resources/";
        var icsFile = new ICALViewer(pathName);
        var bookedRoom = new Booking("1","X000000", LocalDate.now(), LocalTime.now(),LocalTime.now().plusMinutes(30),"Oonga Boonga",13);
        icsFile.writeTo(bookedRoom);
    }

    @Test void shouldRead_AndRetrieveVEvent(){
        String pathName = "./src/test/resources/";
        var icsFile = new ICALViewer(pathName);

        icsFile.readTo();
    }


}