package com.ems.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class LoggingConfig {
    static {
        try (InputStream is = LoggingConfig.class.getClassLoader().getResourceAsStream("logging.properties")) {
            if (is != null) {
                LogManager.getLogManager().readConfiguration(is);
            } else {
                // Fallback or default configuration if needed
                System.err.println("Could not find logging.properties. Using default logger configuration.");
            }
        } catch (IOException e) {
            System.err.println("Error initializing logging: " + e.getMessage());
        }
    }

    public static Logger getLogger(Class<?> clazz) {
        return Logger.getLogger(clazz.getName());
    }
}

