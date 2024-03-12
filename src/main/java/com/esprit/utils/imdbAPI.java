package com.esprit.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class imdbAPI {
    public static void main(String[] args) {
        String query = "spiderwoman";
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String scriptUrl = "https://script.google.com/macros/s/AKfycbyeuvvPJ2jljewXKStVhiOrzvhMPkAEj5xT_cun3IRWc9XEF4F64d-jimDvK198haZk/exec?query=" + encodedQuery;
            System.out.println(scriptUrl);

            // Send the request
            URL url = new URL(scriptUrl);
            int statusCode = 403;
            HttpURLConnection conn = null;
            do {

                System.out.println("Code=" + statusCode);
                // Send the request
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                statusCode = conn.getInputStream().read();
                System.out.println("Status Code: " + conn.getResponseCode());

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
            System.out.println(response);
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray results = jsonResponse.getJSONArray("results");

            // Extract the IMDb URL of the first result
            if (results.length() > 0) {
                JSONObject firstResult = results.getJSONObject(0);
                String imdbUrl = firstResult.getString("imdb");
                System.out.println("IMDb URL of the first result: " + imdbUrl);
            } else {
                System.out.println("No results found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
