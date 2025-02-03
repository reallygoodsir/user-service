package org.servlets;

import org.validators.SecurityValidator;
import org.validators.UserValidator;
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
        String token = request.getHeader("Authorization");
        SecurityValidator securityValidator = new SecurityValidator();
        if (!securityValidator.isValid(token)) {
            logger.error("Token not valid");
            ErrorDetails error = new ErrorDetails("The call is not allowed");
            ErrorDetailsConverter errorDetailsConverter = new ErrorDetailsConverter();
            String errorJson = errorDetailsConverter.convert(error);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            try (PrintWriter writer = response.getWriter()) {
                writer.write(errorJson);
            }
        }

        try {
            String userId = request.getParameter("id");
            if (userId == null || userId.isEmpty()) {
                UserDAO userDAO = new UserDAOImpl();
                Users users = userDAO.getAllUsers();
                UserConverter userConverter = new UserConverter();
                String json = userConverter.convertUsersToJson(users);
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                try (PrintWriter writer = response.getWriter()) {
                    writer.write(json);
                }
            } else {
                UserValidator userValidator = new UserValidator();
                if (userValidator.isPositiveNumber(userId)) {
                    UserDAO userDAO = new UserDAOImpl();
                    Optional<User> optionalUser = userDAO.getUser(Integer.parseInt(userId));
                    if (optionalUser.isPresent()) {
                        User user = optionalUser.get();
                        UserConverter userConverter = new UserConverter();
                        String json = userConverter.convertUserToJson(user);
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        try (PrintWriter writer = response.getWriter()) {
                            writer.write(json);
                        }
                    } else {
                        String errorMessage = "User does not exist with id " + userId;
                        ErrorDetails error = new ErrorDetails(errorMessage);
                        ErrorDetailsConverter errorDetailsConverter = new ErrorDetailsConverter();
                        String errorJson = errorDetailsConverter.convert(error);
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.setContentType("application/json");
                        try (PrintWriter writer = response.getWriter()) {
                            writer.write(errorJson);
                        }
                    }
                } else {
                    String errorMessage = "User id must be positive number. Current value is not correct: " + userId;
                    ErrorDetails error = new ErrorDetails(errorMessage);
                    ErrorDetailsConverter errorDetailsConverter = new ErrorDetailsConverter();
                    String errorJson = errorDetailsConverter.convert(error);
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.setContentType("application/json");
                    try (PrintWriter writer = response.getWriter()) {
                        writer.write(errorJson);
                    }
                }
            }
        } catch (Exception exception) {
            logger.error("Error in doGet", exception);
            ErrorDetails error = new ErrorDetails("Error to get user(s)");
            ErrorDetailsConverter errorDetailsConverter = new ErrorDetailsConverter();
            String errorJson = errorDetailsConverter.convert(error);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
