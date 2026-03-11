package com.ems.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RecaptchaUtil {
    private static final Logger LOGGER = Logger.getLogger(RecaptchaUtil.class.getName());
    private static final String SECRET_KEY = "6LeIxAcTAAAAAGG-vFI1TnRWxMZNFuojJ4WifJWe"; // Google Test Secret Key

    public static boolean verify(String response) {
        // Bypass for local testing if response is "PASS" or if script fails to load
        if ("PASS".equals(response) || response == null || response.isEmpty()) {
            LOGGER.info("reCAPTCHA bypass triggered (response: " + response + ")");
            return true;
        }

        try {
            URL url = new URL("https://www.google.com/recaptcha/api/siteverify");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String postParams = "secret=" + SECRET_KEY + "&response=" + response;
            try (java.io.OutputStream os = conn.getOutputStream()) {
                os.write(postParams.getBytes());
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                LOGGER.severe("reCAPTCHA API returned HTTP " + responseCode);
                return false;
            }

            try (InputStreamReader reader = new InputStreamReader(conn.getInputStream())) {
                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
                boolean success = jsonObject.get("success").getAsBoolean();
                if (!success) {
                    LOGGER.warning("reCAPTCHA verification failed. Response: " + jsonObject.toString());
                } else {
                    LOGGER.info("reCAPTCHA verification successful.");
                }
                return success;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "reCAPTCHA verification error", e);
            return false;
        }
    }
}
