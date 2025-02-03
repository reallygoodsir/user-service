package org.validators;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserValidator {
    private static final Logger LOGGER = LogManager.getLogger(UserValidator.class);

    public boolean isPositiveNumber(String userId) {
        try {
            return Integer.parseInt(userId) > 0;
        } catch (Exception exception) {
            LOGGER.error("User id is not correct {}", userId, exception);
            return false;
        }
    }
}
