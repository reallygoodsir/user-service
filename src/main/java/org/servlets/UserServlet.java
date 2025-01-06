package org.servlets;


import org.dao.UserDAO;
import org.dao.UserDAOImpl;
import org.models.User;
import org.models.Users;
import org.parsers.UserConverter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            UserDAO userDAO = new UserDAOImpl();
            if (request.getParameter("id") != null) {
                Optional<User> optionalUser = userDAO.getUser(Integer.parseInt(request.getParameter("id")));
                if (optionalUser.isPresent()) {
                    User user = optionalUser.get();
                    UserConverter userConverter = new UserConverter();
                    String json = userConverter.convertUserToJson(user);
                    response.setStatus(200);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");

                    try (PrintWriter writer = response.getWriter()) {
                        writer.write(json);
                    }
                } else {
                    System.err.println("Couldn't get the user");
                    response.setStatus(500);
                }
            } else {
                Users allUsers = userDAO.getAllUsers();
                if (!allUsers.getUsers().isEmpty()) {
                    UserConverter userConverter = new UserConverter();
                    String json = userConverter.convertUsersToJson(allUsers);
                    response.setStatus(200);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");

                    try (PrintWriter writer = response.getWriter()) {
                        writer.write(json);
                    }
                } else {
                    response.setStatus(500);
                    System.err.println("Couldn't convert users to json");
                }
            }
        } catch (Exception exception) {
            System.err.println("Error in doGet");
            exception.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            String body = getBody(request);

            UserConverter userConverter = new UserConverter();
            User user = userConverter.convert(body);

            UserDAO userDAO = new UserDAOImpl();
            int userId = userDAO.createUser(user);
            if (userId != 0) {
                System.out.println("Created user id: " + userId);
                user.setId(userId);

                String json = userConverter.convertUserToJson(user);
                if (!json.isEmpty()) {
                    response.setStatus(200);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");

                    try (PrintWriter writer = response.getWriter()) {
                        writer.write(json);
                    }
                } else {
                    response.setStatus(500);
                    System.err.println("Couldn't convert updated user to json");
                }
            } else {
                response.setStatus(500);
                System.err.println("Couldn't convert the user");
            }
        } catch (Exception exception) {
            System.err.println("Error in doPost");
            exception.printStackTrace();
        }
    }

    public static String getBody(HttpServletRequest request) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        try (BufferedReader bufferedReader = request.getReader()) {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException exception) {
            throw new IOException("Error reading the request payload", exception);
        }
        return stringBuilder.toString();
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) {
        try {
            String body = getBody(request);
            UserConverter userConverter = new UserConverter();
            User user = userConverter.convert(body);
            UserDAO userDAO = new UserDAOImpl();

            int userIsUpdated = userDAO.updateUser(user);
            if (userIsUpdated == 1) {
                String json = userConverter.convertUserToJson(user);
                if (!json.isEmpty()) {
                    response.setStatus(200);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    try (PrintWriter writer = response.getWriter()) {
                        writer.write(json);
                    }
                } else {
                    response.setStatus(500);
                    System.err.println("Couldn't convert the user");
                }
            }
        } catch (Exception exception) {
            System.err.println("Error in doPut");
            exception.printStackTrace();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        try {
            String idParameter = request.getParameter("id");
            if (!idParameter.isEmpty()) {
                int id = Integer.parseInt(idParameter);
                UserDAO userDAO = new UserDAOImpl();
                int i = userDAO.deleteUser(id);
                if (i == 1) {
                    response.setStatus(200);
                    System.out.println("Successfully deleted user with the id " + id);
                } else {
                    response.setStatus(500);
                    System.out.println("Couldn't delete a user with the id " + id);
                }
            } else {
                System.err.println("No id provided");
                response.setStatus(500);
            }
        }catch (Exception exception){
            System.err.println("Error in doDelete");
            exception.printStackTrace();
        }
    }
}
