package com.movieticket.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseManager {

    private static final DbConfig config = new DbConfig();

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("class loaded");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }

    private DatabaseManager() {
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(
                config.getUrl(),
                config.getUser(),
                config.getPassword()
            );
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database", e);
        }
    }

    public static void closeConnection() {
        // Maintained for backward compatibility
    }
}