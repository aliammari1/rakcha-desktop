package com.esprit.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.logging.Logger;

public class Chat {
    private static final Logger LOGGER = Logger.getLogger(Chat.class.getName());
    Connection connection;

    public Chat() {
        this.connection = DataSource.getInstance().getConnection();
    }

    /**
     * @param response
     * @return String
     */
    public static String extractContentFromResponse(final String response) {
        final int startMarker = response.indexOf("content") + 11; // Marqueur pour le début du contenu
        final int endMarker = response.indexOf('"', startMarker); // Marqueur pour la fin du contenu
        return response.substring(startMarker, endMarker); // Retourner la sous-chaîne contenant uniquement la réponse
    }

    /**
     * @return String
     */
    /*
     * public String badword(String message) {
     * String question =
     * "detecter si le discours de contenu de la publication est haineux ou non si il ya haineux votre reponse doit etre 100% correcte afficher 1 si non afficher 0 just dit 0 ou 1 idont need anything else  "
     * + message ;
     * try {
     * String completion = chatGPT(question);
     * return completion;
     * } catch (RuntimeException e) {
     * // Gérer les erreurs liées à l'API ChatGPT
     * LOGGER.info("Erreur lors de la détection de mots grossiers : " +
     * e.getMessage());
     * return "-1"; // Retourner une valeur d'erreur spécifique
     * }
     * }
     */
    public String chatGPT(final String message) {
        final String apiKey = System.getenv("OPENAI_API_KEY");
        final String model = System.getenv("OPENAI_MODEL");
        // String url = "https://api.openai.com/v1/chat/completions";
        final String url = "https://api.openai.com/v1/chat/completions";
        try {
            // Créer la requête HTTP POST
            final URL obj = new URL(url);
            final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + apiKey);
            con.setRequestProperty("Content-Type", "application/json");
            // Construire le corps de la requête
            // String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\":
            // \"user\", \"content\": \"" + message + "\"}]}";
            final String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + message
                    + "\"}]}";
            con.setDoOutput(true);
            final OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream(), StandardCharsets.UTF_8);
            writer.write(body);
            writer.flush();
            writer.close();
            // Vérifier le code de réponse HTTP
            final int responseCode = con.getResponseCode();
            if (HttpURLConnection.HTTP_OK == responseCode) {
                // Lire la réponse
                final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
                String inputLine;
                final StringBuilder response = new StringBuilder();
                while (null != (inputLine = in.readLine())) {
                    response.append(inputLine);
                }
                in.close();
                // Extraire et retourner le contenu de la réponse
                return Chat.extractContentFromResponse(response.toString());
            } else {
                // Gérer les erreurs HTTP
                throw new RuntimeException(
                        "Échec de la récupération de la réponse de l'API ChatGPT. Code d'erreur HTTP : "
                                + responseCode);
            }
        } catch (final IOException e) {
            // Gérer les erreurs d'E/S
            throw new RuntimeException("Erreur lors de l'envoi de la requête à l'API ChatGPT : " + e.getMessage(), e);
        }
    }

    public String badword(final String message) {
        final String question = "detecter si le discours de contenu de la publication est haineux ou non si il ya haineux votre reponse doit etre 100% correcte afficher 1 si non afficher 0 just dit 0 ou 1 idont need anything else "
                + message;
        try {
            final String completion = this.chatGPT(question);
            return completion.trim(); // Assurez-vous de supprimer les espaces inutiles autour de la réponse
        } catch (final RuntimeException e) {
            // Gérer les erreurs liées à l'API ChatGPT
            Chat.LOGGER.info("Erreur lors de la détection de mots grossiers : " + e.getMessage());
            return "-1"; // Retourner une valeur d'erreur spécifique
        }
    }
}
