package org.dao;

import org.models.User;
import org.models.Users;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class UserDAOImpl implements UserDAO {
    private static final String ADD_USER = "insert into users (name, age, birth_date) values (?, ?, ?)";
    private static final String UPDATE_USER = "update users set name = ?, age = ?, birth_date = ? where id = ?";
    private static final String GET_ALL_USERS = "select * from users";
    private static final String GET_USER = "select * from users where id = ?";
    private static final String DELETE_USER = "delete from users where id = ?";
    @Override
    public int createUser(User user) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmtInsertUser = connection.prepareStatement(ADD_USER, Statement.RETURN_GENERATED_KEYS)) {
            stmtInsertUser.setString(1, user.getName());
            stmtInsertUser.setInt(2, user.getAge());

            java.sql.Date birthDate = new java.sql.Date(user.getBirthDate().getTime());
            stmtInsertUser.setDate(3, birthDate);

            stmtInsertUser.executeUpdate();
            ResultSet generatedKeys = stmtInsertUser.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new Exception("Couldn't get generated keys");
            }
        } catch (Exception exception) {
            System.err.println("Error while creating user");
            exception.printStackTrace();
            return 0;
        }
    }

    @Override
    public int updateUser(User user) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmtUpdateUser = connection.prepareStatement(UPDATE_USER)) {
            stmtUpdateUser.setString(1, user.getName());
            stmtUpdateUser.setInt(2, user.getAge());
            java.sql.Date birthDate = new java.sql.Date(user.getBirthDate().getTime());
            stmtUpdateUser.setDate(3, birthDate);
            stmtUpdateUser.setInt(4, user.getId());
            stmtUpdateUser.executeUpdate();
            return 1;
        } catch (Exception exception) {
            System.err.println("Error while updating user");
            exception.printStackTrace();
            return 0;
        }
    }

    @Override
    public Users getAllUsers() {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmtGetAllUsers = connection.prepareStatement(GET_ALL_USERS)) {
            ResultSet resultSet = stmtGetAllUsers.executeQuery();
            List<User> allUsers = new ArrayList<>();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                java.sql.Date sqlDate = resultSet.getDate("birth_date");
                java.util.Date birthDate = new java.util.Date(sqlDate.getTime());
                User user = new User(id, name, age, birthDate);
                allUsers.add(user);
            }
            if (!allUsers.isEmpty()) {
                return new Users(allUsers);
            } else {
                System.out.println("There are no users in the database");
                return (Users) Collections.emptyList();
            }
        } catch (Exception exception) {
            System.err.println("Error while getting all users");
            exception.printStackTrace();
            return (Users) Collections.emptyList();
        }
    }

    @Override
    public Optional<User> getUser(int id) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmtGetAllUsers = connection.prepareStatement(GET_USER)) {
            stmtGetAllUsers.setInt(1, id);
            ResultSet resultSet = stmtGetAllUsers.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                java.sql.Date sqlDate = resultSet.getDate("birth_date");
                java.util.Date birthDate = new java.util.Date(sqlDate.getTime());

                User user = new User(id, name, age, birthDate);
                return Optional.of(user);
            }else{
                System.out.println("Couldn't find a user with the provided id");
                return Optional.empty();
            }
        } catch (Exception exception) {
            System.err.println("Error while getting a user");
            exception.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public int deleteUser(int id) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmtDeleteUser = connection.prepareStatement(DELETE_USER)) {
            stmtDeleteUser.setInt(1,id);
            return stmtDeleteUser.executeUpdate();
        }catch (Exception exception){
            System.err.println("Error while trying to delete user");
            return 0;
        }
    }


}

