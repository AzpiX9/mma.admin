package org.helmo.mma.admin.infrastructures;

import org.helmo.mma.admin.domains.core.User;
import org.helmo.mma.admin.domains.users.CanReadUsers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserFileRepository implements CanReadUsers {
    private final String filePath;
    //private final List<User> users = new ArrayList<>();
    private final Map<String,User> usersM = new HashMap<>();


    public UserFileRepository(String path) {
        filePath = path;
    }

    //List ou Map ? pour stocker et récupérer les users
    @Override
    public Map<String,User> getUsers() {
        try(BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null){
                var values = line.split(";");
                var userTemp = new User(values[0],values[1],values[2],values[3]);
                //this.users.add(userTemp);
                this.usersM.put(values[0],userTemp);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return this.usersM;
    }

    @Override
    public User getUser(String matricule) {
        return usersM.get(matricule);
    }
}
