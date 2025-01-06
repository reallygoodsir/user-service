package org.dao;

import org.models.User;
import org.models.Users;

import java.util.List;
import java.util.Optional;

public interface UserDAO extends GeneralDAO{
    int createUser(User user);
    int updateUser(User user);
    Users getAllUsers();
    Optional<User> getUser(int id);
    int deleteUser(int id);
}
