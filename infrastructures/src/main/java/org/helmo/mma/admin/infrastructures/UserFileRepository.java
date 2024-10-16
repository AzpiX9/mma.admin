package org.helmo.mma.admin.infrastructures;

import org.helmo.mma.admin.domains.core.User;
import org.helmo.mma.admin.domains.exceptions.UserException;
import org.helmo.mma.admin.domains.users.CanReadUsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class UserFileRepository implements CanReadUsers {
    private final String filePath;
    private final List<User> users = new ArrayList<>();
    //private final Map<String,User> usersM = new HashMap<>();


    public UserFileRepository(String path) {
        filePath = path;
    }

    //List ou Map ? pour stocker et récupérer les users
    @Override
    public List<User> getUsers() {
        users.clear();
        try(BufferedReader br = Files.newBufferedReader(Path.of(filePath))) {
            String line;
            while ((line = br.readLine()) != null){
                var values = line.split(";");
                var userTemp = new User(values[0],values[1],values[2],values[3]);
                this.users.add(userTemp);
                //this.usersM.put(values[0],userTemp);
            }
        } catch (IOException e) {
            throw new UserException("Aucun fichier utilisateur");
        }

        return this.users;
    }

    @Override
    public User getUser(String matricule) {
        return getUsers()
                .stream()
                .filter(user -> user.Matricule().equals(matricule))
                .findFirst().orElse(new User("X123456","Doe","John","j.doe@helmo.be"));
    }

    @Override
    public boolean exists(String matricule) {
        boolean exists = false;

        for (User user : getUsers()) {
            if (user.Matricule().equals(matricule)) {
                exists = true;
                break;
            }
        }

        return exists;
    }
}
