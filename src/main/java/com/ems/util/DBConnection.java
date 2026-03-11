package com.ems.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {
    private static final String CONFIG_FILE = "config.properties";
    private static final Properties properties = new Properties();
    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());

    static {
        try (java.io.InputStream input = DBConnection.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                LOGGER.warning("Unable to find " + CONFIG_FILE + ". Using hardcoded defaults.");
            } else {
                properties.load(input);
            }
            Class.forName("org.postgresql.Driver");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing DBConnection", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = properties.getProperty("db.url", "jdbc:postgresql://localhost:5432/ems_db");
        String user = properties.getProperty("db.user", "postgres");
        String password = properties.getProperty("db.password", "");
        
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to connect to the database!", e);
            throw e;
        }
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Failed to close the database connection!", e);
            }
        }
    }
}

