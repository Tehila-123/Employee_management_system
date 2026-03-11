package com.ems.util;

import java.sql.Connection;
import java.sql.SQLException;

public class TestPostgresConnection {
    public static void main(String[] args) {
        System.out.println("Attempting to connect to PostgreSQL...");
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                System.out.println("Successfully connected to PostgreSQL!");
                System.out.println("Database: " + conn.getMetaData().getDatabaseProductName());
                System.out.println("Version: " + conn.getMetaData().getDatabaseProductVersion());
            } else {
                System.out.println("Failed to establish connection (returned null).");
            }
        } catch (SQLException e) {
            System.err.println("Connection failed!");
            e.printStackTrace();
        }
    }
}
