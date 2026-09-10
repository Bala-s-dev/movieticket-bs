package com.movieticket.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseManager {
    private static final DbConfig config = new DbConfig();
    public static Connection conn;

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
            conn = DriverManager.getConnection(
                config.getUrl(),
                config.getUser(),
                config.getPassword()
            );
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database", e);
        }
    }

    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to close database connection", e);
        }
    }
}