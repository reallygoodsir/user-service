package org.servlets;

import org.validators.UserValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dao.UserDAO;
import org.dao.UserDAOImpl;
import org.models.ErrorDetails;
import org.models.User;
import org.models.Users;
import org.converters.ErrorDetailsConverter;
import org.converters.UserConverter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

public class UserServlet extends HttpServlet {
    private static final Logger LOGGER = LogManager.getLogger(UserServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
            LOGGER.error("Error in doGet", exception);
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
            User user = userConverter.convertToUser(body);
            UserValidator userValidator = new UserValidator();
            boolean isUserValid = userValidator.isValid(user);
            if (isUserValid) {
                UserDAO userDAO = new UserDAOImpl();
                int userId = userDAO.createUser(user);
                LOGGER.info("Created user id: {}", userId);
                user.setId(userId);
                String json = userConverter.convertUserToJson(user);
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                try (PrintWriter writer = response.getWriter()) {
                    writer.write(json);
                }
            } else {
                String errorMessage = "User data is not valid.";
                ErrorDetails error = new ErrorDetails(errorMessage);
                ErrorDetailsConverter errorDetailsConverter = new ErrorDetailsConverter();
                String errorJson = errorDetailsConverter.convert(error);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                try (PrintWriter writer = response.getWriter()) {
                    writer.write(errorJson);
                }
            }
        } catch (Exception exception) {
            LOGGER.error("Error in doPost", exception);
            ErrorDetails error = new ErrorDetails("Internal server error");
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
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String body = getBody(request);
            UserConverter userConverter = new UserConverter();
            User user = userConverter.convertToUser(body);
            UserValidator userValidator = new UserValidator();
            boolean isUserValid = userValidator.isValid(user);
            if (isUserValid) {
                UserDAO userDAO = new UserDAOImpl();
                int userIsUpdated = userDAO.updateUser(user);
                if (userIsUpdated == 1) {
                    String json = userConverter.convertUserToJson(user);
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    try (PrintWriter writer = response.getWriter()) {
                        writer.write(json);
                    }
                } else {
                    throw new Exception("User was not updated");
                }
            }
        } catch (Exception exception) {
            LOGGER.error("Error in doPut", exception);
            ErrorDetails error = new ErrorDetails("Internal server error");
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
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String userIdParameter = request.getParameter("id");
            if (userIdParameter != null && !userIdParameter.isEmpty()) {
                UserValidator userValidator = new UserValidator();
                boolean isUserIdValid = userValidator.isPositiveNumber(userIdParameter);
                if (isUserIdValid) {
                    int userId = Integer.parseInt(userIdParameter);
                    UserDAO userDAO = new UserDAOImpl();
                    int deletedUser = userDAO.deleteUser(userId);
                    if (deletedUser == 1) {
                        response.setStatus(200);
                        LOGGER.info("Successfully deleted user with the id {}", userId);
                    } else {
                        LOGGER.error("Couldn't delete a user with the id {}", userId);
                        ErrorDetails error = new ErrorDetails("Couldn't delete a user with the id " + userId);
                        ErrorDetailsConverter errorDetailsConverter = new ErrorDetailsConverter();
                        String errorJson = errorDetailsConverter.convert(error);
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.setContentType("application/json");
                        try (PrintWriter writer = response.getWriter()) {
                            writer.write(errorJson);
                        }
                    }
                } else {
                    LOGGER.error("User id is not valid {}", userIdParameter);
                    ErrorDetails error = new ErrorDetails("User id is not valid " + userIdParameter);
                    ErrorDetailsConverter errorDetailsConverter = new ErrorDetailsConverter();
                    String errorJson = errorDetailsConverter.convert(error);
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.setContentType("application/json");
                    try (PrintWriter writer = response.getWriter()) {
                        writer.write(errorJson);
                    }
                }
            } else {
                LOGGER.error("User id is null or empty {}", userIdParameter);
                ErrorDetails error = new ErrorDetails("User id is null or empty " + userIdParameter);
                ErrorDetailsConverter errorDetailsConverter = new ErrorDetailsConverter();
                String errorJson = errorDetailsConverter.convert(error);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                try (PrintWriter writer = response.getWriter()) {
                    writer.write(errorJson);
                }
            }
        } catch (Exception exception) {
            LOGGER.error("Error in doDelete");
            ErrorDetails error = new ErrorDetails("Error in doDelete");
            ErrorDetailsConverter errorDetailsConverter = new ErrorDetailsConverter();
            String errorJson = errorDetailsConverter.convert(error);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
