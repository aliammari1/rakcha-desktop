package com.esprit.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class imdbAPI {
    private static final Logger LOGGER = Logger.getLogger(imdbAPI.class.getName());

    /**
     * @param args
     */
    public static void main(final String[] args) {
        final String query = "spiderwoman";
        try {
            final String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            final String scriptUrl = "https://script.google.com/macros/s/AKfycbyeuvvPJ2jljewXKStVhiOrzvhMPkAEj5xT_cun3IRWc9XEF4F64d-jimDvK198haZk/exec?query="
                    + encodedQuery;
            imdbAPI.LOGGER.info(scriptUrl);
            // Send the request
            final URL url = new URL(scriptUrl);
            int statusCode = 403;
            HttpURLConnection conn = null;
            do {
                imdbAPI.LOGGER.info("Code=" + statusCode);
                // Send the request
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                statusCode = conn.getInputStream().read();
                imdbAPI.LOGGER.info("Status Code: " + conn.getResponseCode());
            } while (123 != statusCode);
            // Read the response
            final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            final StringBuilder responseBuilder = new StringBuilder();
            String line;
            while (null != (line = reader.readLine())) {
                responseBuilder.append(line);
            }
            final String response = "{" + responseBuilder + "}";
            reader.close();
            // Parse the JSON response
            imdbAPI.LOGGER.info(response);
            final JSONObject jsonResponse = new JSONObject(response);
            final JSONArray results = jsonResponse.getJSONArray("results");
            // Extract the IMDb URL of the first result
            if (0 < results.length()) {
                final JSONObject firstResult = results.getJSONObject(0);
                final String imdbUrl = firstResult.getString("imdb");
                imdbAPI.LOGGER.info("IMDb URL of the first result: " + imdbUrl);
            } else {
                imdbAPI.LOGGER.info("No results found.");
            }
        } catch (final Exception e) {
            imdbAPI.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
