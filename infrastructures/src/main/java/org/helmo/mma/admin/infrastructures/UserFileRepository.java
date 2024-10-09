package org.helmo.mma.admin.infrastructures;

import org.helmo.mma.admin.domains.core.User;
import org.helmo.mma.admin.domains.users.CanReadUsers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserFileRepository implements CanReadUsers {
    private final String filePath;

    public UserFileRepository(String path) {
        filePath = path;
    }

    @Override
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null){
                var values = line.split(";");

                users.add(new User(values[0],values[1],values[2],values[3]));
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return users;
    }
}
