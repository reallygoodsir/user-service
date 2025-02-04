package org.converters;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.models.User;
import org.models.Users;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class UserConverter {
    private static final Logger logger = LogManager.getLogger(UserConverter.class);

    public User convertToUser(String jsonContent) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            return objectMapper.readValue(jsonContent, User.class);
        } catch (IOException exception) {
            logger.error("Error converting json to user", exception);
            throw new RuntimeException("Error in JSON Converter");
        }
    }

    public String convertUserToJson(User user) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            return objectMapper.writeValueAsString(user);
        } catch (IOException exception) {
            logger.error("Error converting user to json", exception);
            throw new RuntimeException("Error in JSON Converter");
        }
    }

    public String convertUsersToJson(Users users) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(users);
        } catch (Exception exception) {
            logger.error("Error converting users to json", exception);
            throw new RuntimeException("Error in JSON Converter");
        }
    }
}

