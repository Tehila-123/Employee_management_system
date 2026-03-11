package com.ems.util;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.InputStream;
import java.io.IOException;

public class EmailUtil {
    private static final Logger LOGGER = Logger.getLogger(EmailUtil.class.getName());
    
    private static final String CONFIG_FILE = "config.properties";
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = EmailUtil.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                LOGGER.warning("Unable to find " + CONFIG_FILE + ". Using default console fallback.");
            } else {
                properties.load(input);
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error loading configuration file", ex);
        }
    }

    public static void sendOTP(String recipientEmail, String otp) {
        String host = properties.getProperty("mail.smtp.host", "smtp.gmail.com");
        String port = properties.getProperty("mail.smtp.port", "587");
        String emailFrom = properties.getProperty("mail.smtp.user");
        String emailPassword = properties.getProperty("mail.smtp.password");

        if (emailFrom == null || emailPassword == null || emailFrom.contains("your-email")) {
            printConsoleFallback(recipientEmail, otp);
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", properties.getProperty("mail.smtp.auth", "true"));
        props.put("mail.smtp.starttls.enable", properties.getProperty("mail.smtp.starttls.enable", "true"));
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailFrom, emailPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailFrom));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("EMS - Your Two-Factor Authentication Code");
            message.setText("Your OTP for Employee Management System is: " + otp + "\nThis code expires in 5 minutes.");

            Transport.send(message);
            LOGGER.info("OTP sent successfully to " + recipientEmail);
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Failed to send email to " + recipientEmail + ". Falling back to console.", e);
            printConsoleFallback(recipientEmail, otp);
        }
    }

    private static void printConsoleFallback(String recipientEmail, String otp) {
        System.out.println("======================================================");
        System.out.println("DEVELOPMENT OTP DELIVERED TO CONSOLE (Real Email Not Configured)");
        System.out.println("To: " + recipientEmail);
        System.out.println("OTP Code: " + otp);
        System.out.println("======================================================");
        LOGGER.info("OTP printed to console due to missing or placeholder credentials.");
    }
}
