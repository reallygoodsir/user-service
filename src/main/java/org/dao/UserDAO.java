package org.dao;

import org.models.User;
import org.models.Users;

import java.util.Optional;

public interface UserDAO extends GeneralDAO {
    int createUser(User user) throws Exception;

    int updateUser(User user) throws Exception;

    Users getAllUsers() throws Exception;

    Optional<User> getUser(int id) throws Exception;

    int deleteUser(int id) throws Exception;
}
