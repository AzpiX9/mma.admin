package org.helmo.mma.admin.infrastructures;

import org.helmo.mma.admin.domains.core.Room;
import org.helmo.mma.admin.domains.rooms.CanReadRooms;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RoomDbRepository implements CanReadRooms {

    private final Connection connection;

    public RoomDbRepository(Connection dbUrl) {
        this.connection = Objects.requireNonNull(dbUrl);
    }

    @Override
    public List<Room> getRooms() {
        var rooms = new ArrayList<Room>();
        var query = "SELECT * FROM Rooms";

        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                rooms.add(new Room(
                        resultSet.getString("idRoom"),
                        resultSet.getString("roomName"),
                        resultSet.getInt("roomSize")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rooms;
    }

    @Override
    public Room getRoom(String roomId) {
        var query = "SELECT * FROM Rooms WHERE idRoom = ?";

        try(var stmt = connection.prepareStatement(query)) {
            stmt.setString(1, roomId);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return new Room(
                        resultSet.getString("idRoom"),
                        resultSet.getString("roomName"),
                        resultSet.getInt("roomSize")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
