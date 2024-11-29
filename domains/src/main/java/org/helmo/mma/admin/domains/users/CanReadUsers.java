package org.helmo.mma.admin.domains.users;

import org.helmo.mma.admin.domains.core.User;

import java.util.List;
import java.util.Map;

public interface CanReadUsers {
    List<User> getUsers();

    User getUser(String matricule); //TODO: déplacer

    boolean exists(String matricule); //TODO : ditto
}
