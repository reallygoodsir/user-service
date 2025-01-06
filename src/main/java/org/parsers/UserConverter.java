package org.parsers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.models.User;
import org.models.Users;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class UserConverter {
    public User convert(String jsonContent) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            return objectMapper.readValue(jsonContent, User.class);
        } catch (IOException exception) {
            System.err.println("Error parsing JSON content\n" + exception.getMessage());
            throw new RuntimeException("Error in JSON Parser");
        }
    }

    public String convertUserToJson(User user) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            return objectMapper.writeValueAsString(user);
        } catch (IOException exception) {
            System.err.println("Error converting User to JSON\n" + exception.getMessage());
            throw new RuntimeException("Error in JSON Converter");
        }
    }
    public String convertUsersToJson(Users users) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            // Set date format to match the desired output (e.g., "1991-10-31")
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

            // Convert the Users object to JSON
            return objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(users);

        } catch (Exception exception) {
            System.err.println("Error converting Users to JSON");
            exception.printStackTrace();
            throw new RuntimeException("Error in Users to JSON conversion", exception);
        }
    }
}

