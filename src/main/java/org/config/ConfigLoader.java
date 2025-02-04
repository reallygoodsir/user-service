package org.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static Config CONFIG;

    public static Config loadConfig() {
        if (CONFIG == null) {
            Properties properties = new Properties();
            try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
                if (input == null) {
                    throw new IOException("config.properties file not found in resources folder");
                }
                properties.load(input);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load configuration", e);
            }

            CONFIG = new Config(
                    properties.getProperty("authorization.token"),
                    properties.getProperty("db.url"),
                    properties.getProperty("db.username"),
                    properties.getProperty("db.password")
            );
        }
        return CONFIG;
    }
}
