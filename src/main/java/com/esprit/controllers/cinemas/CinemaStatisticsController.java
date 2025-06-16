package com.esprit.controllers.cinemas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esprit.models.cinemas.CinemaComment;
import com.esprit.services.cinemas.CinemaCommentService;
import com.esprit.services.cinemas.CinemaService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Is responsible for generating and displaying sentiment statistics for various
 * cinemas based on user comments. It retrieves movie commentary data from a
 * service, generates sentiment statistics for each cinema using the comments,
 * and creates PieCharts to visualize the statistics. The controller also
 * displays the generated PieCharts in an AnchorPane.
 */
public class CinemaStatisticsController {
    private final CinemaCommentService cinemaCommentService = new CinemaCommentService();
    private final CinemaService cinemaService = new CinemaService();
    @FXML
    private AnchorPane statisticsAnchor;

    /**
     * Clears an existing children container, retrieves comments from a database,
     * generates sentiment statistics for each cinema, and displays the statistics
     * as PieCharts within a new container added to the parent AnchorPane.
     *
     * @param event
     *            ActionEvent triggered by the button click that initiates the code
     *            execution and calls the `showStatistics()` method.
     *            <p>
     *            - `event`: an instance of `ActionEvent`, representing a
     *            user-generated event
     */
    @FXML
    void showStatistics(final ActionEvent event) {
        this.statisticsAnchor.getChildren().clear();
        // Récupérer les commentaires de la base de données
        final List<CinemaComment> comments = this.cinemaCommentService.read();
        // Générer les statistiques sur l'analyse de sentiment
        final Map<String, Map<String, Integer>> cinemaSentimentStatistics = this.generateSentimentStatistics(comments);
        // Créer un VBox pour contenir les PieCharts
        final VBox chartContainer = new VBox();
        chartContainer.setSpacing(20);
        // Afficher les statistiques dans des PieCharts
        for (final Map.Entry<String, Map<String, Integer>> entry : cinemaSentimentStatistics.entrySet()) {
            final PieChart pieChart = this.createPieChart(entry.getKey(), entry.getValue());
            chartContainer.getChildren().add(pieChart);
        }
        // Ajouter le VBox à l'AnchorPane
        this.statisticsAnchor.getChildren().add(chartContainer);
    }

    /**
     * @param comments
     * @return Map<String, Map < String, Integer>>
     */
    // Méthode pour générer les statistiques sur l'analyse de sentiment pour chaque
    // cinéma

    /**
     * Generates a map of sentiment statistics for each cinema based on a list of
     * comments, where each comment is associated with a cinema and a sentiment
     * label. The function counts the number of comments with each sentiment and
     * stores it in the map for that cinema.
     *
     * @param comments
     *            list of comments that will be processed to generate sentiment
     *            statistics for each cinema.
     *            <p>
     *            - It is a list of `CinemaComment` objects, representing user
     *            comments on cinema movies.
     * @returns a map of cinema names to sentiment statistics, where each cinema's
     *          sentiment is counted and stored as an integer value.
     *          <p>
     *          - The output is a map of cinema names to maps of sentiment names to
     *          integer counts of the number of comments expressing that sentiment
     *          for each cinema. - Each cinema name in the outer map corresponds to
     *          a map of sentiment names in the inner map, indicating the number of
     *          comments expressing that sentiment for that cinema. - The maps are
     *          keyed by sentiment name and contain integer values representing the
     *          count of comments expressing that sentiment for each cinema.
     */
    private Map<String, Map<String, Integer>> generateSentimentStatistics(final List<CinemaComment> comments) {
        final Map<String, Map<String, Integer>> cinemaSentimentStatistics = new HashMap<>();
        // Parcourir les commentaires et compter le nombre de sentiments pour chaque
        // cinéma
        for (final CinemaComment comment : comments) {
            final String cinemaName = this.cinemaService.getCinemaById(comment.getCinema().getId()).getName();
            final String sentiment = comment.getSentiment();
            // Vérifier si le cinéma est déjà dans la map
            if (cinemaSentimentStatistics.containsKey(cinemaName)) {
                final Map<String, Integer> sentimentStatistics = cinemaSentimentStatistics.get(cinemaName);
                sentimentStatistics.put(sentiment, sentimentStatistics.getOrDefault(sentiment, 0) + 1);
            } else {
                final Map<String, Integer> sentimentStatistics = new HashMap<>();
                sentimentStatistics.put(sentiment, 1);
                cinemaSentimentStatistics.put(cinemaName, sentimentStatistics);
            }
        }
        return cinemaSentimentStatistics;
    }

    // Méthode pour créer un PieChart pour un cinéma donné

    /**
     * Creates a PieChart object and sets its title based on a cinema name and
     * sentiment statistics map. It then adds the chart data based on the map
     * entries.
     *
     * @param cinemaName
     *            name of the cinema for which sentiment statistics are being
     *            generated and displayed in the PieChart.
     * @param sentimentStatistics
     *            sentiment statistics of a cinema, which is used to generate a pie
     *            chart displaying the distribution of sentiment scores for that
     *            cinema.
     *            <p>
     *            - `Map<String, Integer>`: This map contains sentiment statistics
     *            for different cinema names, where each key represents a cinema
     *            name and the value represents the number of reviews with a
     *            positive sentiment towards that cinema.
     * @returns a pie chart representing the sentiment statistics of a given cinema.
     *          <p>
     *          1/ Title: The title of the PieChart is set to the cinema name
     *          followed by " Sentiment Statistics". 2/ Data: The function adds the
     *          sentiment statistics data to the PieChart's data list. Each entry in
     *          the data list consists of a category (key) and the corresponding
     *          frequency (value).
     */
    private PieChart createPieChart(final String cinemaName, final Map<String, Integer> sentimentStatistics) {
        final PieChart pieChart = new PieChart();
        pieChart.setTitle(cinemaName + " Sentiment Statistics");
        // Ajouter les données au PieChart
        for (final Map.Entry<String, Integer> entry : sentimentStatistics.entrySet()) {
            pieChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }
        return pieChart;
    }
}
