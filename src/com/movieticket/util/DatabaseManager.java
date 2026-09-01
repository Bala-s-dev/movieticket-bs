package com.movieticket.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseManager {

    private static final DbConfig CONFIG = DbConfig.load();
    private static boolean driverLoaded = false;

    private DatabaseManager() { }

    public static Connection getConnection() throws SQLException {
        ensureDriverLoaded();
        return DriverManager.getConnection(CONFIG.getUrl(), CONFIG.getUser(), CONFIG.getPassword());
    }

    public static void testConnection() throws SQLException {
        try (Connection connection = getConnection()) {
            System.out.println("Successfully connected to the database: " + connection.getMetaData().getURL());
        }
    }

    private static synchronized void ensureDriverLoaded() {
        if (!driverLoaded) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                driverLoaded = true;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("MySQL JDBC Driver not found. Please include it in your library path.", e);
            }
        }
    }
}
