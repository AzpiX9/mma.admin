package org.helmo.mma.admin.domains.users;

import org.helmo.mma.admin.domains.core.User;

import java.util.List;

public interface CanReadUsers {
    List<User> getUsers();
}
