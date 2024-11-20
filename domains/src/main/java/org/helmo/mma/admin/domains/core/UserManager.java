package org.helmo.mma.admin.domains.core;

import org.helmo.mma.admin.domains.exceptions.UserException;

import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private List<User> users = new ArrayList<>();

    public UserManager(List<User> users) {
        this.users = users;
    }

    public User getUserFromMatr(String mat) {
        return users
                .stream()
                .filter(user -> user.Matricule().equals(mat))
                .findFirst().orElse(new User("X123456","Doe","John","j.doe@helmo.be"));
    }

    public void existsFromMatr(String mat) {
        for (User user : users) {
            if (user.Matricule().equals(mat)) {
                return;
            }
        }

        throw new UserException("Utilisateur non trouvé");
    }
}
