package com.esprit.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esprit.services.produits.AvisService;

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
    public static void main(String[] args) {
        String query = "spiderwoman";
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String scriptUrl = "https://script.google.com/macros/s/AKfycbyeuvvPJ2jljewXKStVhiOrzvhMPkAEj5xT_cun3IRWc9XEF4F64d-jimDvK198haZk/exec?query="
                    + encodedQuery;
            LOGGER.info(scriptUrl);
            // Send the request
            URL url = new URL(scriptUrl);
            int statusCode = 403;
            HttpURLConnection conn = null;
            do {
                LOGGER.info("Code=" + statusCode);
                // Send the request
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                statusCode = conn.getInputStream().read();
                LOGGER.info("Status Code: " + conn.getResponseCode());
            } while (statusCode != 123);
            // Read the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }
            String response = "{" + responseBuilder + "}";
            reader.close();
            // Parse the JSON response
            LOGGER.info(response);
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray results = jsonResponse.getJSONArray("results");
            // Extract the IMDb URL of the first result
            if (results.length() > 0) {
                JSONObject firstResult = results.getJSONObject(0);
                String imdbUrl = firstResult.getString("imdb");
                LOGGER.info("IMDb URL of the first result: " + imdbUrl);
            } else {
                LOGGER.info("No results found.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
