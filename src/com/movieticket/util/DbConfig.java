package com.movieticket.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public final class DbConfig {

    private final String url;
    private final String user;
    private final String password;

    private DbConfig(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public static DbConfig load() {
        Properties props = new Properties();

        try (InputStream fileStream = new FileInputStream("db.properties")) {
            props.load(fileStream);
        } catch (IOException ignoredFileNotFound) {
            try (InputStream cpStream = DbConfig.class.getClassLoader().getResourceAsStream("db.properties")) {
                if (cpStream != null) {
                    props.load(cpStream);
                }
            } catch (IOException ignoredClasspathNotFound) {
                
            }
        }

        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        return new DbConfig(url, user, password);
    }

    public String getUrl() { return url; }
    public String getUser() { return user; }
    public String getPassword() { return password; }
}
