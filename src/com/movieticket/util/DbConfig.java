package com.movieticket.util;

import java.io.InputStream;
import java.util.Properties;

public class DbConfig {
    Properties properties;
    String url;
    String user;
    String password;

    DbConfig(){
        properties = new Properties();
        try {
            InputStream inputStream = getClass()
                    .getClassLoader()
                    .getResourceAsStream("db.properties");

            if (inputStream == null) {
                throw new RuntimeException("db.properties not found");
            }

            properties.load(inputStream);

            url = properties.getProperty("db.url");
            user = properties.getProperty("db.user");
            password = properties.getProperty("db.password");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUrl(){
        return url;
    }
    public String getUser(){
        return user;
    }
    public String getPassword(){
        return password;
    }

}
