package org.servlets;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dao.UserDAO;
import org.dao.UserDAOImpl;
import org.models.ErrorDetails;
import org.models.User;
import org.models.Users;
import org.parsers.ErrorDetailsConverter;
import org.parsers.UserConverter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

public class UserServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(UserServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
                    response.setStatus(500);
                    logger.error("Couldn't get the user");
                    ErrorDetails error = new ErrorDetails("Couldn't get the user");
                    ErrorDetailsConverter errorDetailsConverter = new ErrorDetailsConverter();
                    String errorJson = errorDetailsConverter.convert(error);
                    response.setContentType("application/json");
                    try (PrintWriter writer = response.getWriter()) {
                        writer.write(errorJson);
                    }
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
                    logger.error("Couldn't get all users");
                    ErrorDetails error = new ErrorDetails("Couldn't get all users");
                    ErrorDetailsConverter errorDetailsConverter = new ErrorDetailsConverter();
                    String errorJson = errorDetailsConverter.convert(error);
                    response.setContentType("application/json");
                    try (PrintWriter writer = response.getWriter()) {
                        writer.write(errorJson);
                    }
                }
            }
        } catch (Exception exception) {
            response.setStatus(500);
            logger.error("Error in doGet");
            ErrorDetails error = new ErrorDetails("Error in doGet");
            ErrorDetailsConverter errorDetailsConverter = new ErrorDetailsConverter();
            String errorJson = errorDetailsConverter.convert(error);
            response.setContentType("application/json");
            try (PrintWriter writer = response.getWriter()) {
                writer.write(errorJson);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String body = getBody(request);

            UserConverter userConverter = new UserConverter();
            User user = userConverter.convert(body);

            UserDAO userDAO = new UserDAOImpl();
            int userId = userDAO.createUser(user);
            if (userId != 0) {
                logger.info("Created user id: {}", userId);
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
                    logger.error("Couldn't convert the updated user to json");
                    ErrorDetails error = new ErrorDetails("Couldn't convert the updated user to json");
                    ErrorDetailsConverter errorDetailsConverter = new ErrorDetailsConverter();
                    String errorJson = errorDetailsConverter.convert(error);
                    response.setContentType("application/json");
                    try (PrintWriter writer = response.getWriter()) {
                        writer.write(errorJson);
                    }
                }
            } else {
                response.setStatus(500);
                logger.error("Couldn't create a user");
                ErrorDetails error = new ErrorDetails("Couldn't create a user");
                ErrorDetailsConverter errorDetailsConverter = new ErrorDetailsConverter();
                String errorJson = errorDetailsConverter.convert(error);
                response.setContentType("application/json");
                try (PrintWriter writer = response.getWriter()) {
                    writer.write(errorJson);
                }
            }
        } catch (Exception exception) {
            response.setStatus(500);
            logger.error("Error in doPost");
            ErrorDetails error = new ErrorDetails("Error in doPost");
            ErrorDetailsConverter errorDetailsConverter = new ErrorDetailsConverter();
            String errorJson = errorDetailsConverter.convert(error);
            response.setContentType("application/json");
            try (PrintWriter writer = response.getWriter()) {
                writer.write(errorJson);
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
                    logger.error("Couldn't convert the user");
                    ErrorDetails error = new ErrorDetails("Couldn't convert the user");
                    ErrorDetailsConverter errorDetailsConverter = new ErrorDetailsConverter();
                    String errorJson = errorDetailsConverter.convert(error);
                    response.setContentType("application/json");
                    try (PrintWriter writer = response.getWriter()) {
                        writer.write(errorJson);
                    }
                }
            }
        } catch (Exception exception) {
            response.setStatus(500);
            logger.error("Error in doPut");
            ErrorDetails error = new ErrorDetails("Error in doPut");
            ErrorDetailsConverter errorDetailsConverter = new ErrorDetailsConverter();
            String errorJson = errorDetailsConverter.convert(error);
            response.setContentType("application/json");
            try (PrintWriter writer = response.getWriter()) {
                writer.write(errorJson);
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String idParameter = request.getParameter("id");
            if (idParameter != null) {
                int id = Integer.parseInt(idParameter);
                UserDAO userDAO = new UserDAOImpl();
                int i = userDAO.deleteUser(id);
                if (i == 1) {
                    response.setStatus(200);
                    logger.info("Successfully deleted user with the id {}", id);
                } else {
                    response.setStatus(500);
                    logger.error("Couldn't delete a user with the id {}", id);
                    ErrorDetails error = new ErrorDetails("Couldn't delete a user with the id " + id);
                    ErrorDetailsConverter errorDetailsConverter = new ErrorDetailsConverter();
                    String errorJson = errorDetailsConverter.convert(error);
                    response.setContentType("application/json");
                    try (PrintWriter writer = response.getWriter()) {
                        writer.write(errorJson);
                    }
                }
            } else {
                response.setStatus(500);
                logger.error("No id provided");
                ErrorDetails error = new ErrorDetails("No id provided");
                ErrorDetailsConverter errorDetailsConverter = new ErrorDetailsConverter();
                String errorJson = errorDetailsConverter.convert(error);
                response.setContentType("application/json");
                try (PrintWriter writer = response.getWriter()) {
                    writer.write(errorJson);
                }
            }
        } catch (Exception exception) {
            response.setStatus(500);
            logger.error("Error in doDelete");
            ErrorDetails error = new ErrorDetails("Error in doDelete");
            ErrorDetailsConverter errorDetailsConverter = new ErrorDetailsConverter();
            String errorJson = errorDetailsConverter.convert(error);
            response.setContentType("application/json");
            try (PrintWriter writer = response.getWriter()) {
                writer.write(errorJson);
            }
        }
    }

    private static String getBody(HttpServletRequest request) throws IOException {
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
}
