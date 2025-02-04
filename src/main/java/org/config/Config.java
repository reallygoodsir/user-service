package org.config;

public class Config {
    private String authorizationToken;
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;

    public Config(String authorizationToken, String dbUrl, String dbUsername, String dbPassword) {
        this.authorizationToken = authorizationToken;
        this.dbUrl = dbUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    @Override
    public String toString() {
        return "Config{" +
                "authorizationToken='" + authorizationToken + '\'' +
                ", dbUrl='" + dbUrl + '\'' +
                ", dbUsername='" + dbUsername + '\'' +
                ", dbPassword='" + dbPassword + '\'' +
                '}';
    }
}

