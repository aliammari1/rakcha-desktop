package com.esprit.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Utility class providing helper methods for the RAKCHA application. Contains
 * reusable functionality and common operations.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class ImdbAPI {
    private static final Logger LOGGER = Logger.getLogger(ImdbAPI.class.getName());
    private static final String API_URL = "https://script.google.com/macros/s/AKfycbyeuvvPJ2jljewXKStVhiOrzvhMPkAEj5xT_cun3IRWc9XEF4F64d-jimDvK198haZk/exec";
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 1000;

    /**
     * Search for a movie on IMDb
     *
     * @param query
     *            The movie title to search for
     * @return The IMDb URL of the first matching result, or null if not found
     * @throws IOException
     *             if there's an error communicating with the API
     */
    public static String searchMovie(final String query) throws IOException {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Search query cannot be empty");
        }

        final String encodedQuery = URLEncoder.encode(query.trim(), StandardCharsets.UTF_8);
        final String scriptUrl = API_URL + "?query=" + encodedQuery;

        LOGGER.info("Searching IMDb for: " + query);

        int retries = 0;
        while (retries < MAX_RETRIES) {
            HttpURLConnection conn = null;
            try {
                final URL url = new URL(scriptUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }

                        JSONObject jsonResponse = new JSONObject("{" + response + "}");
                        JSONArray results = jsonResponse.getJSONArray("results");

                        if (results.length() > 0) {
                            String imdbUrl = results.getJSONObject(0).getString("imdb");
                            LOGGER.info("Found IMDb URL: " + imdbUrl);
                            return imdbUrl;
                        }

                        LOGGER.info("No results found for: " + query);
                        return null;
                    }
                } else if (conn.getResponseCode() == 429) {
                    // Too Many Requests - implement retry with backoff
                    retries++;
                    if (retries < MAX_RETRIES) {
                        Thread.sleep(RETRY_DELAY_MS * retries);
                        continue;
                    }
                }

                throw new IOException("HTTP error code: " + conn.getResponseCode());

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Search interrupted", e);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }

        throw new IOException("Max retries exceeded while searching IMDb");
    }
}
