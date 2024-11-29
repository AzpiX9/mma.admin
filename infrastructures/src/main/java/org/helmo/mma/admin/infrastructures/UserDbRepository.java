package org.helmo.mma.admin.infrastructures;

import org.helmo.mma.admin.domains.core.User;
import org.helmo.mma.admin.domains.users.CanReadUsers;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserDbRepository implements CanReadUsers {

    private Connection connection;

    public UserDbRepository(Connection dbUrl) {
        this.connection = Objects.requireNonNull(dbUrl);
    }

    @Override
    public List<User> getUsers() {
        var users = new ArrayList<User>();
        var query = "SELECT * FROM Member";
        try (var stmt = connection.prepareStatement(query); var rs = stmt.executeQuery()) {
            while (rs.next()) {
                var nameSplit = rs.getString("userFullName").split(" ");
                users.add(new User(
                        rs.getString("userMatr"),
                        nameSplit[1],
                        nameSplit[0],
                        rs.getString("userMail")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    @Override
    public User getUser(String matricule) {
        var query = "SELECT * FROM Member WHERE userMatr=?";
        try (var stmt = connection.prepareStatement(query)) {
            stmt.setString(1, matricule);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    var nameSplited = rs.getString("userFullName").split(" ");
                    return new User(
                            rs.getString("userMatr"),
                            nameSplited[1],
                            nameSplited[0],
                            rs.getString("userMail")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public boolean exists(String matricule) {

        for (User user : getUsers()) {
            if (user.Matricule().equals(matricule)) {
                return true;
            }
        }

        return false;
    }
}
