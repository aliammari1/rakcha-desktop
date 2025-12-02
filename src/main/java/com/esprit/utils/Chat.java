package com.esprit.utils;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class providing helper methods for the RAKCHA application. Contains
 * reusable functionality and common operations.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class Chat {

    private static final Logger LOGGER = Logger.getLogger(Chat.class.getName());
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private final Connection connection;
    private final Dotenv dotenv = Dotenv.load();

    /**
     * Constructor for Chat class that initializes the database connection.
     */
    public Chat() {
        this.connection = DataSource.getInstance().getConnection();
    }


    /**
     * Extracts the content from an OpenAI API response.
     *
     * @param response The JSON response string from the OpenAI API
     * @return The extracted content string
     */
    private static String extractContentFromResponse(final String response) {
        final int startMarker = response.indexOf("content") + 11;
        final int endMarker = response.indexOf('"', startMarker);
        return response.substring(startMarker, endMarker).trim();
    }


    /**
     * Sends a message to ChatGPT and returns the response.
     *
     * @param message the message to send to ChatGPT
     * @return the ChatGPT response
     */
    public String chatGPT(final String message) {
        final String apiKey = dotenv.get("OPENAI_API_KEY");
        final String model = dotenv.get("OPENAI_MODEL");

        if (apiKey == null || model == null) {
            throw new RuntimeException("OpenAI API credentials not found in config");
        }


        HttpURLConnection conn = null;
        try {
            final URL obj = new URL(API_URL);
            conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            final String body = String.format(
                "{\"model\": \"%s\", \"messages\": [{\"role\": \"user\", \"content\": \"%s\"}]}", model,
                message.replace("\"", "\\\""));

            try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8)) {
                writer.write(body);
            }


            if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
                try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    return extractContentFromResponse(response.toString());
                }

            } else {
                throw new RuntimeException(
                    "Failed to get response from OpenAI API. HTTP error code: " + conn.getResponseCode());
            }

        } catch (IOException e) {
            throw new RuntimeException("Error communicating with OpenAI API: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }

        }

    }


    /**
     * Checks if a message contains inappropriate or hateful content.
     *
     * @param message the message to check for inappropriate content
     * @return "1" if content is inappropriate, "0" if appropriate, "-1" if error
     * occurred
     */
    public String badword(final String message) {
        final String question = "Is this content hateful or inappropriate? Reply with only '1' for yes or '0' for no: "
            + message;
        try {
            return chatGPT(question).trim();
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Failed to check content for inappropriate language", e);
            return "-1";
        }

    }

}

