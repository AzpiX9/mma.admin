package org.helmo.mma.admin.infrastructures;

import org.helmo.mma.admin.domains.core.Room;
import org.helmo.mma.admin.domains.exceptions.RoomException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoomFileRepositoryTest {
    private File fileTest;
    private List<Room> expectedRooms = new ArrayList<>();
    private static final String VALID_PATH = "src/test/resources/rooms.csv";

    @Test
    public void shouldThrows_Exception_WhenPathIsEmpty() {
        buildFile("");
        var roomReader = new RoomFileRepository("");
        RoomException roomException = assertThrows(RoomException.class,roomReader::getRooms);
        assertEquals(roomException.getMessage(),"Aucune salle n'est disponible");
    }

    @Test
    public void shouldRetrieve_DataFromFile_AndChecksItsSize() {
        buildFile(VALID_PATH);
        var roomReader = new RoomFileRepository(VALID_PATH);

        assertTrue(!roomReader.getRooms().isEmpty());
        assertTrue(roomReader.getRooms().size() == 3);
        assertEquals(expectedRooms,roomReader.getRooms());
    }

    @Test
    public void shouldRetrieve_OneRoomAndItsValues() {
        buildFile(VALID_PATH);
        var roomReader = new RoomFileRepository(VALID_PATH);

        var expected = new Room("L1","Local 1",5);
        var actual = roomReader.getRoom("L1");
        assertEquals(expected,actual);
    }

    @Test
    public void roomGiven_ShouldExist(){
        buildFile(VALID_PATH);
        var roomReader = new RoomFileRepository(VALID_PATH);
        var idGiven = "L2";

        assertTrue(roomReader.getRooms()
                .contains(roomReader.getRoom(idGiven)));
    }

    @Test
    public void roomGiven_ShouldNotExist_AndThrowRoomException(){
        buildFile(VALID_PATH);
        var roomReader = new RoomFileRepository(VALID_PATH);
        var idGiven = "L5";

        var roomException = assertThrows(RoomException.class,() -> roomReader.getRoom(idGiven));
        assertEquals("Aucune salle n'est trouvé",roomException.getMessage());

    }

    private void buildFile(String path){
        if(path.isEmpty() || path.isBlank()){
            return;
        }
        expectedRooms.add(new Room("L1","Local 1",5));
        expectedRooms.add(new Room("L2","Local 2",7));
        expectedRooms.add(new Room("L3","Local 3",9));
        fileTest = new File(path);
        try(BufferedWriter bw = Files.newBufferedWriter(Paths.get(fileTest.getPath()))){
            for (Room room : expectedRooms) {
                bw.write(String.format("%s;%s;%s",room.Id(),room.Name(),room.Size()));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @AfterEach
    void tearDown(){
        if(fileTest == null){
            return;
        }
        if(!fileTest.exists()){
            return;
        }
        fileTest.delete();
    }
}