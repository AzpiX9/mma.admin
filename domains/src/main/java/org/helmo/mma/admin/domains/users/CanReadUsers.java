package org.helmo.mma.admin.domains.users;

import org.helmo.mma.admin.domains.core.User;
import org.helmo.mma.admin.domains.exceptions.UserException;

import java.util.List;
import java.util.Map;

public interface CanReadUsers {
    List<User> getUsers() throws UserException;

    User getUser(String matricule); //TODO: déplacer

    boolean exists(String matricule); //TODO : ditto
}
