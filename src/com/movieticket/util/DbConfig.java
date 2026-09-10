package com.movieticket.util;

import java.io.InputStream;
import java.util.Properties;

public class DbConfig {
    private final Properties properties;
    private final String url;
    private final String user;
    private final String password;

    public DbConfig() {
        properties = new Properties();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("db.properties")) {

            if (inputStream == null) {
                throw new RuntimeException("db.properties not found");
            }

            properties.load(inputStream);

            url = properties.getProperty("db.url");
            user = properties.getProperty("db.user");
            password = properties.getProperty("db.password");
            // System.out.println(url);
            // System.out.println(user);

            if (url == null || user == null || password == null) {
                throw new RuntimeException("Database configuration is missing in db.properties");
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}