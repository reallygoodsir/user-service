package org.dao;

import org.models.User;
import org.models.Users;

import java.util.Optional;

public interface UserDAO extends GeneralDAO {
    int createUser(User user);

    int updateUser(User user);

    Users getAllUsers() throws Exception;

    Optional<User> getUser(int id) throws Exception;

    int deleteUser(int id);
}
