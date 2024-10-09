/*
 * This source file is an example
 */
package org.helmo.mma.admin.infrastructures;

import org.helmo.mma.admin.domains.core.Room;
import org.helmo.mma.admin.domains.rooms.CanReadRooms;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RoomFileRepository implements CanReadRooms {

    private final String filePath;

    public RoomFileRepository(String path) {
        filePath = path;
    }

    @Override
    public List<Room> getRooms() {
        List<Room> rooms = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null){
                var values = line.split(";");

                rooms.add(new Room(values[0],values[1],Integer.parseInt(values[2])));
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return rooms;
    }
}
