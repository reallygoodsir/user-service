package org.dao;

import org.config.ConfigLoader;

public interface GeneralDAO {
    String DB_URL = ConfigLoader.loadConfig().getDbUrl();
    String USER = ConfigLoader.loadConfig().getDbUsername();
    String PASS = ConfigLoader.loadConfig().getDbPassword();
}
