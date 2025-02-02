package org.parsers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.models.ErrorDetails;

public class ErrorDetailsConverter {
    private static final Logger logger = LogManager.getLogger(ErrorDetailsConverter.class);

    public String convert(ErrorDetails errorDetails) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(errorDetails);
        } catch (Exception exception) {
            logger.error("Error while converting error details to json");
            throw new RuntimeException("Error while converting the error details to json");
        }
    }
}
